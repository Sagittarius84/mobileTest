package org.noorganization.instalist.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import junit.framework.Assert;

import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.RecipeProvider;
import org.noorganization.instalist.provider.internal.TagProvider;
import org.noorganization.instalist.provider.internal.UnitProvider;

/**
 * Created by Tino on 29.10.2015.
 */
public class ProviderTestUtils {


    /**
     * Insert an Unit.
     * @param _contentResolver the content resolver.
     * @param _name the name of the unit.
     * @return null or the uri of the inserted unit.
     */
    public static Uri insertUnit(ContentResolver _contentResolver, String _name){
        ContentValues unitValues = new ContentValues();
        unitValues.put(Unit.COLUMN.NAME, _name);
        return _contentResolver.insert(Uri.parse(UnitProvider.MULTIPLE_UNIT_CONTENT_URI), unitValues);
    }

    public static Unit getUnit(ContentResolver _contentResolver, Uri _uri){
        Cursor unitCursor = _contentResolver.query(_uri, Unit.COLUMN.ALL_COLUMNS, null, null,null);
        if(unitCursor == null){
            return null;
        }
        if(unitCursor.getCount()== 0){
            unitCursor.close();
            return null;
        }

        unitCursor.moveToFirst();
        Unit unit = new Unit();
        unit.mUUID = unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.ID));
        unit.mName = unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.NAME));
        unitCursor.close();

        return unit;
    }
    /**
     * Inserts a product into the current database.
     *
     * @param _productProvider the productProvider to insert the new product.
     * @param _uuid            the generated uuid.
     * @param _name            the name of the product.
     * @param _defaultAmount   the default amount.
     * @param _stepAmount      the step amount.
     * @param _unit_ID         (String) null) when there should be no unit, else the id of the corresponding unit.
     * @return the uri for the inserted product.
     */
    public static Uri insertProduct(IInternalProvider _productProvider, String _uuid, String _name, float _defaultAmount, float _stepAmount, String _unit_ID) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Product.COLUMN.ID, _uuid);
        contentValues.put(Product.COLUMN.NAME, _name);
        contentValues.put(Product.COLUMN.DEFAULT_AMOUNT, _defaultAmount);
        contentValues.put(Product.COLUMN.STEP_AMOUNT, _stepAmount);
        contentValues.put(Product.COLUMN.UNIT, _unit_ID);

        return _productProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", _uuid)), contentValues);
    }

    /**
     * Inserts a product into the current database. Generates a dynamic uuid.
     *
     * @param _contentResolver the productProvider to insert the new product.
     * @param _name            the name of the product.
     * @param _defaultAmount   the default amount.
     * @param _stepAmount      the step amount.
     * @param _unit_ID         (String) null) when there should be no unit, else the id of the corresponding unit.
     * @return the uri for the inserted product.
     */
    public static Uri insertProduct(ContentResolver _contentResolver, String _name, float _defaultAmount, float _stepAmount, String _unit_ID) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(Product.COLUMN.NAME, _name);
        contentValues.put(Product.COLUMN.DEFAULT_AMOUNT, _defaultAmount);
        contentValues.put(Product.COLUMN.STEP_AMOUNT, _stepAmount);
        contentValues.put(Product.COLUMN.UNIT, _unit_ID);

        return _contentResolver.insert(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), contentValues);
    }

    public static Product getProduct(ContentResolver _contentResolver, String _UUID) {
        Cursor productCursor = _contentResolver.query(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", _UUID)), Product.COLUMN.ALL_COLUMNS, null, null, null);
        if(productCursor == null){
            throw new NullPointerException("ProductCursor is null!");
        }
        if(productCursor.getCount() == 0){
            productCursor.close();
            throw new NullPointerException("ProductCursor does not contain any elements!");
        }
        productCursor.moveToFirst();

        Cursor unitCursor = _contentResolver.query(
                Uri.parse(UnitProvider.SINGLE_UNIT_CONTENT_URI.replace("*", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.UNIT)))),
                Unit.COLUMN.ALL_COLUMNS, null, null, null);

        if(unitCursor == null){
            throw new NullPointerException("UnitCursor is null!");
        }

        if(unitCursor.getCount() == 0){
            unitCursor.close();
            throw new NullPointerException("UnitCursor does not contain any elements!");
        }
        unitCursor.moveToFirst();

        Product product = new Product();
        product.mUUID = productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.ID));
        product.mName = productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.NAME));
        product.mDefaultAmount = productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.DEFAULT_AMOUNT));
        product.mStepAmount = productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.STEP_AMOUNT));

        Unit unit = new Unit();
        unit.mUUID = unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.ID));
        unit.mName = unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.NAME));

        product.mUnit = unit;
        productCursor.close();
        unitCursor.close();
        return product;
    }

    /**
     * Inserts a tag into the database.
     *
     * @param _tagProvider the tagprovider where the actions should be done.
     * @param _uuid        the uuid of the tag.
     * @param tagName      the name of the tag.
     * @return uri of the inserted tag.
     */
    public static Uri insertTag(IInternalProvider _tagProvider, String _uuid, String tagName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tag.COLUMN.ID, _uuid);
        contentValues.put(Tag.COLUMN.NAME, tagName);

        return _tagProvider.insert(Uri.parse(TagProvider.SINGLE_TAG_CONTENT_URI.replace("*", _uuid)), contentValues);
    }

    /**
     * Inserts a tag into the database.
     *
     * @param _contentResolver the tagprovider where the actions should be done.
     * @param tagName          the name of the tag.
     * @return uri of the inserted tag.
     */
    public static Uri insertTag(ContentResolver _contentResolver, String tagName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tag.COLUMN.NAME, tagName);

        return _contentResolver.insert(Uri.parse(TagProvider.MULTIPLE_TAG_CONTENT_URI), contentValues);
    }
    /**
     * Get a tag by Uri.
     *
     * @param _contentResolver the tagprovider where the actions should be done.
     * @param _uri          the uri for the tag.
     * @return the tag or null.
     */
    public static Tag getTag(ContentResolver _contentResolver, Uri _uri) {
        Cursor tagCursor = _contentResolver.query(_uri, Tag.COLUMN.ALL_COLUMNS, null, null,null);
        if(tagCursor == null){
            return null;
        }
        if(tagCursor.getCount()== 0){
            tagCursor.close();
            return null;
        }

        tagCursor.moveToFirst();
        Tag tag = new Tag();
        tag.mUUID = tagCursor.getString(tagCursor.getColumnIndex(Tag.COLUMN.ID));
        tag.mName = tagCursor.getString(tagCursor.getColumnIndex(Tag.COLUMN.NAME));
        tagCursor.close();

        return tag;
    }
    /**
     * Inserts a recipe to the database.
     *
     * @param mRecipeProvider the recipeprovider.
     * @param uuidRecipe      the uuid of the recipe.
     * @param name            the name of the recipe.
     * @return the uri of the inserted recipe or null if failure happened.
     */
    public static Uri insertRecipe(IInternalProvider mRecipeProvider, String uuidRecipe, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Recipe.COLUMN.ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN.NAME, name);
        // insert at begin a recipe
        return mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);
    }

    /**
     * Inserts a recipe to the database.
     *
     * @param _contentResolver the recipeprovider.
     * @param _name            the name of the recipe.
     * @return the uri of the inserted recipe or null if failure happened.
     */
    public static Uri insertRecipe(ContentResolver _contentResolver, String _name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Recipe.COLUMN.NAME, _name);
        // insert at begin a recipe
        return _contentResolver.insert(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), contentValues);
    }

    public static void deleteTestCategories(ContentResolver _resolver) {
        Cursor all = _resolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                Category.COLUMN.ALL_COLUMNS,
                null,
                null,
                null);

        Assert.assertNotNull(all);
        if (!all.moveToFirst() || all.getCount() == 0) return;

        do {
            String id = all.getString(all.getColumnIndex(Category.COLUMN.ID));
            if (id.equals("-")) {
                continue;
            }
            Assert.assertEquals(1, _resolver.delete(
                    Uri.withAppendedPath(
                            InstalistProvider.BASE_CONTENT_URI,
                            "category/" + id)
                    , null, null));
        } while (all.moveToNext());
    }

    public static void deleteTestLists(ContentResolver _resolver, String _categoryUUID) {
        Cursor all = _resolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" + _categoryUUID + "/list"),
                ShoppingList.COLUMN.ALL_COLUMNS,
                null,
                null,
                null);

        Assert.assertNotNull(all);
        if (!all.moveToFirst() || all.getCount() == 0) return;

        do {
            String id = all.getString(all.getColumnIndex(ShoppingList.COLUMN.ID));

            Assert.assertEquals(1, _resolver.delete(
                    Uri.withAppendedPath(
                            InstalistProvider.BASE_CONTENT_URI,
                            "category/" + _categoryUUID + "/list/" + id)
                    , null, null));
        } while (all.moveToNext());
    }

    public static void deleteTestEntries(ContentResolver _resolver, String _categoryUUID, String _shoppingListUUID) {
        Cursor all = _resolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" + _categoryUUID + "/list/" + _shoppingListUUID + "/entry"),
                ListEntry.COLUMN.ALL_COLUMNS,
                null,
                null,
                null);

        Assert.assertNotNull(all);
        if (!all.moveToFirst() || all.getCount() == 0) return;

        do {
            String id = all.getString(all.getColumnIndex(ShoppingList.COLUMN.ID));
            Assert.assertEquals(1, _resolver.delete(
                    Uri.withAppendedPath(
                            InstalistProvider.BASE_CONTENT_URI,
                            "category/" + _categoryUUID + "/list/" + _shoppingListUUID + "entry/" + id)
                    , null, null));
        } while (all.moveToNext());
    }

}
