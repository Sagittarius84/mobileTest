package org.noorganization.instalist.controller.implementation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.controller.event.ListItemChangedMessage;
import org.noorganization.instalist.controller.event.ProductChangedMessage;
import org.noorganization.instalist.controller.event.RecipeChangedMessage;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.provider.internal.IngredientProvider;
import org.noorganization.instalist.provider.internal.ListEntryProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;

import java.util.List;

import de.greenrobot.event.EventBus;


public class ProductController implements IProductController {

    private static String LOG_TAG = ProductController.class.getName();
    private static ProductController mInstance;

    private EventBus mBus;
    private Context mContext;
    private ContentResolver mResolver;
    private IUnitController mUnitController;

    private ProductController(Context _context) {
        mBus = EventBus.getDefault();
        mContext = _context;
        mResolver = mContext.getContentResolver();
        mUnitController = ControllerFactory.getUnitController();
    }

    static ProductController getInstance(Context _context) {
        if (mInstance == null) {
            mInstance = new ProductController(_context);
        }
        return mInstance;
    }

    @Override
    public Product createProduct(String _name, Unit _unit, float _defaultAmount, float _stepAmount) {
        if (_name == null || _name.length() == 0) {
            return null;
        }
        if (Float.isInfinite(_defaultAmount) || Float.isNaN(_defaultAmount) || _defaultAmount <= 0.0f) {
            return null;
        }
        if (Float.isInfinite(_stepAmount) || Float.isNaN(_stepAmount) || _stepAmount < 0.0f) {
            return null;
        }

        Unit unit = mUnitController.findById(_unit.mUUID);
        if (unit == null) {
            return null;
        }

        Cursor productCursor = mResolver.query(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), Product.COLUMN.ALL_COLUMNS, Product.COLUMN.NAME + "= ?", new String[]{_name}, null, null);
        if (productCursor == null || productCursor.getCount() > 0) {
            return null;
        }

        Product product = new Product();
        product.mName = _name;
        product.mDefaultAmount = _defaultAmount;
        product.mStepAmount = _stepAmount;
        product.mUnit = unit;

        Uri productUri = mResolver.insert(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), product.toContentValues());
        if (productUri == null) {
            return null;
        }

        mBus.post(new ProductChangedMessage(Change.CREATED, product));

        productCursor.close();
        return product;
    }

    @Override
    public Product findById(@NonNull String _uuid) {
        Cursor productCursor = mResolver.query(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", _uuid)), Product.COLUMN.ALL_COLUMNS, null, null, null);
        if (productCursor == null || productCursor.getCount() != 1) {
            return null;
        }
        productCursor.moveToFirst();
        Product product = new Product();
        product.mName = productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.NAME));
        product.mUUID = productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.ID));
        product.mDefaultAmount = productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.DEFAULT_AMOUNT));
        product.mStepAmount = productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.STEP_AMOUNT));
        product.mUnit = mUnitController.findById(productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.UNIT)));

        /*
        if(product.mUnit == null){
            return null;
        }
        */
        productCursor.close();
        return product;
    }

    @Override
    public Product modifyProduct(Product _toChange) {
        if (_toChange == null) {
            return null;
        }
        Product oldProduct = this.findById(_toChange.mUUID);
        if (oldProduct == null) {
            return null;
        }
        if (_toChange.mName == null || _toChange.mName.length() == 0) {
            return oldProduct;
        }
        if (Float.isInfinite(_toChange.mDefaultAmount) || Float.isNaN(_toChange.mDefaultAmount) ||
                _toChange.mDefaultAmount <= 0.0f) {
            return oldProduct;
        }
        if (Float.isInfinite(_toChange.mStepAmount) || Float.isNaN(_toChange.mStepAmount) ||
                _toChange.mStepAmount < 0.0f) {
            return oldProduct;
        }
        // check if given unit exists
        if (_toChange.mUnit != null && mUnitController.findById(_toChange.mUnit.mUUID) == null) {
            return oldProduct;
        }

        // check if another product has the same name if this name was changed
        Cursor productsWithSameName = mResolver.query(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI),
                Product.COLUMN.ALL_COLUMNS, Product.COLUMN.NAME + "= ? AND " + Product.COLUMN.ID + "<> ?", new String[]{_toChange.mName, _toChange.mUUID}, null, null);
        if (productsWithSameName == null) {
            Log.e(LOG_TAG, "Internal failure ");
            return oldProduct;
        }
        // check if there is another item with the same name
        if (productsWithSameName.getCount() > 0) {
            return oldProduct;
        }
        productsWithSameName.close();

        _toChange.mUUID = oldProduct.mUUID;

        Uri productUri = mResolver.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", _toChange.mUUID)), _toChange.toContentValues());
        if (productUri == null) {
            return oldProduct;
        }

        mBus.post(new ProductChangedMessage(Change.CHANGED, _toChange));

        return _toChange;
    }

    @Override
    public boolean removeProduct(Product _toRemove, boolean _deleteCompletely) {
        if (_toRemove == null) {
            return false;
        }

        Product foundProduct = findById(_toRemove.mUUID);
        if (foundProduct == null) {
            // no product to delete
            return false;
        }

        if(!_deleteCompletely) {

            Cursor listEntryCursor = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"), ListEntry.COLUMN.ALL_COLUMNS, ListEntry.COLUMN.PRODUCT + "= ?", new String[]{_toRemove.mUUID}, null, null);
            if(listEntryCursor == null){
                Log.e(LOG_TAG, "Query for listEntry was not possible in removeProduct.");
                return false;
            }
            if(listEntryCursor.getCount() > 0){
                Log.e(LOG_TAG, "There are still ListEntries connected to this product. Either set deleteCompletely flag or remove the ListEntries by yourself.");
                listEntryCursor.close();
                return false;
            }
            Cursor ingredientCursor = mResolver.query(Uri.parse(IngredientProvider.MULTIPLE_INGREDIENT_CONTENT_URI), Ingredient.COLUMN.ALL_COLUMNS, Ingredient.COLUMN.PRODUCT_ID + "= ?", new String[]{_toRemove.mUUID}, null, null);
            if(ingredientCursor == null){
                Log.e(LOG_TAG, "Query for ingredient was not possible in removeProduct.");
                return false;
            }

            if(ingredientCursor.getCount() > 0){
                Log.e(LOG_TAG, "There are still ingredients connected to this product. Either set deleteCompletely flag or remove the ingredients by your self.");
                ingredientCursor.close();
                return false;
            }

            listEntryCursor.close();
            ingredientCursor.close();
        }

        // TODO later this day
        for (ListEntry currentEntry : foundEntries) {
            currentEntry.delete();
            mBus.post(new ListItemChangedMessage(Change.DELETED, currentEntry));
        }
        for (Ingredient currentIngredient : foundIngredients) {
            currentIngredient.delete();
            mBus.post(new RecipeChangedMessage(Change.CHANGED,
                    currentIngredient.mRecipe));
        }
        SugarRecord.deleteAll(TaggedProduct.class, "m_product = ?", _toRemove.getId() + "");
        _toRemove.delete();
        mBus.post(new ProductChangedMessage(Change.DELETED, foundProduct));

        return true;
    }

    @Override
    public boolean addTagToProduct(Product _product, Tag _tag) {
        if (_product == null || _tag == null) {
            return false;
        }
        Product foundProduct = SugarRecord.findById(Product.class, _product.getId());
        Tag foundTag = SugarRecord.findById(Tag.class, _tag.getId());
        if (foundProduct == null || foundTag == null) {
            return false;
        }

        long existingTagCount = Select.from(TaggedProduct.class).where(
                Condition.prop(TaggedProduct.ATTR_PRODUCT).eq(foundProduct.getId())).and(
                Condition.prop(TaggedProduct.ATTR_TAG).eq(foundTag.getId())).count();
        if (existingTagCount == 0) {
            TaggedProduct newProductsTag = new TaggedProduct(_tag, _product);
            newProductsTag.save();
            mBus.post(new ProductChangedMessage(Change.CHANGED, foundProduct));
        }

        return true;
    }

    @Override
    public void removeTagFromProduct(Product _product, Tag _tag) {
        if (_product == null || _tag == null) {
            return;
        }

        Product foundProduct = SugarRecord.findById(Product.class, _product.getId());
        if (foundProduct == null) {
            return;
        }

        List<TaggedProduct> taggedProducts = Select.from(TaggedProduct.class).where(
                Condition.prop(TaggedProduct.ATTR_PRODUCT).eq(foundProduct.getId())).and(
                Condition.prop(TaggedProduct.ATTR_TAG).eq(_tag.getId())).list();
        if (taggedProducts.size() != 0) {
            for (TaggedProduct currentToDelete : taggedProducts) {
                currentToDelete.delete();
            }
            mBus.post(new ProductChangedMessage(Change.CHANGED, foundProduct));
        }
    }
}
