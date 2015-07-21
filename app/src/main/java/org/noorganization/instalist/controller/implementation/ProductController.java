package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IProductController;
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

import java.util.List;

import de.greenrobot.event.EventBus;


public class ProductController implements IProductController {

    private static ProductController mInstance;

    private EventBus mBus;

    static ProductController getInstance() {
        if (mInstance == null) {
            mInstance = new ProductController();
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
        if (_unit != null && SugarRecord.findById(Unit.class, _unit.getId()) == null) {
            return null;
        }
        if (Select.from(Product.class).where(Condition.prop("m_name").eq(_name)).count() != 0) {
            return null;
        }

        Product rtn = new Product(_name, _unit, _defaultAmount, _stepAmount);
        rtn.save();

        mBus.post(new ProductChangedMessage(Change.CREATED, rtn));

        return rtn;
    }

    @Override
    public Product modifyProduct(Product _toChange) {
        if (_toChange == null) {
            return null;
        }
        Product rtn = SugarRecord.findById(Product.class, _toChange.getId());
        if (rtn == null) {
            return null;
        }
        if (_toChange.mName == null || _toChange.mName.length() == 0) {
            return rtn;
        }
        if (Float.isInfinite(_toChange.mDefaultAmount) || Float.isNaN(_toChange.mDefaultAmount) ||
                _toChange.mDefaultAmount <= 0.0f) {
            return rtn;
        }
        if (Float.isInfinite(_toChange.mStepAmount) || Float.isNaN(_toChange.mStepAmount) ||
                _toChange.mStepAmount < 0.0f) {
            return rtn;
        }
        if (_toChange.mUnit != null &&
                SugarRecord.findById(Unit.class, _toChange.mUnit.getId()) == null) {
            return rtn;
        }

        for (Product productToCheck : Select.from(Product.class).
                where(Condition.prop("m_name").eq(_toChange.mName)).list()) {
            if (productToCheck.getId().compareTo(_toChange.getId()) != 0) {
                return rtn;
            }
        }

        rtn.mName = _toChange.mName;
        rtn.mUnit = _toChange.mUnit;
        rtn.mDefaultAmount = _toChange.mDefaultAmount;
        rtn.mStepAmount = _toChange.mStepAmount;
        rtn.save();

        mBus.post(new ProductChangedMessage(Change.CHANGED, rtn));

        return rtn;
    }

    @Override
    public boolean removeProduct(Product _toRemove, boolean _deleteCompletely) {
        if (_toRemove == null) {
            return false;
        }

        Product foundProduct = SugarRecord.findById(Product.class, _toRemove.getId());
        if (foundProduct == null) {
            return false;
        }

        List<ListEntry>  foundEntries     = Select.from(ListEntry.class).
                where(Condition.prop(ListEntry.ATTR_PRODUCT).eq(_toRemove.getId())).list();
        List<Ingredient> foundIngredients = Select.from(Ingredient.class).
                where(Condition.prop(Ingredient.ATTR_PRODUCT).eq(_toRemove.getId())).list();
        if (!_deleteCompletely && (foundEntries.size() > 0 || foundIngredients.size() > 0)) {
            return false;
        }

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
        Tag     foundTag     = SugarRecord.findById(Tag.class, _tag.getId());
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

    private ProductController() {
        mBus = EventBus.getDefault();
    }
}
