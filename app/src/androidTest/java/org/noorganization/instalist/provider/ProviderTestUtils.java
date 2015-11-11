package org.noorganization.instalist.provider;

import android.content.ContentValues;
import android.net.Uri;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.RecipeProvider;
import org.noorganization.instalist.provider.internal.TagProvider;

/**
 * Created by Tino on 29.10.2015.
 */
public class ProviderTestUtils {


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
     * Inserts a tag into the database.
     * @param _tagProvider the tagprovider where the actions should be done.
     * @param _uuid the uuid of the tag.
     * @param tagName the name of the tag.
     * @return uri of the inserted tag.
     */
    public static Uri insertTag(IInternalProvider _tagProvider, String _uuid, String tagName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Tag.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, _uuid);
        contentValues.put(Tag.COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME, tagName);

        return _tagProvider.insert(Uri.parse(TagProvider.SINGLE_TAG_CONTENT_URI.replace("*", _uuid)), contentValues);
    }

    /**
     * Inserts a recipe to the database.
     * @param mRecipeProvider the recipeprovider.
     * @param uuidRecipe the uuid of the recipe.
     * @param name the name of the recipe.
     * @return the uri of the inserted recipe or null if failure happened.
     */
    public static Uri insertRecipe(IInternalProvider mRecipeProvider, String uuidRecipe, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Recipe.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME, name);
        // insert at begin a recipe
        return mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);
    }

}
