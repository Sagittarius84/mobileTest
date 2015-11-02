package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;

/**
 * Represents a product as ingredient in a recipe.
 * Created by michi on 14.04.15.
 */
public class Ingredient extends SugarRecord<Ingredient> {

    public final static String TABLE_NAME = "ingredient";

    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_PRODUCT_ID = "product_id";
    public final static String COLUMN_RECIPE_ID = "recipe_id";
    public final static String COLUMN_AMOUNT = "amount";

    public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_PRODUCT_ID, COLUMN_RECIPE_ID, COLUMN_AMOUNT};

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " TEXT PRIMARY KEY,"
            + COLUMN_PRODUCT_ID + " TEXT,"
            + COLUMN_RECIPE_ID + " TEXT,"
            + COLUMN_AMOUNT + " REAL,"
            + "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES " +  Product.TABLE_NAME + "( " + Product.LOCAL_COLUMN_ID  + " )"
            + "ON UPDATE CASCADE ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_RECIPE_ID + ") REFERENCES " +  Recipe.TABLE_NAME + "( " + Recipe.COLUMN_ID  + " )"
            + "ON UPDATE CASCADE ON DELETE CASCADE"
            + ")";
    /**
     * @deprecated use {@link Ingredient#COLUMN_ID}
     */
    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");
    /**
     * @deprecated use {@link Ingredient#COLUMN_AMOUNT}
     */
    public final static String ATTR_AMOUNT = StringUtil.toSQLName("mAmount");
    /**
     * @deprecated use {@link Ingredient#COLUMN_RECIPE_ID}
     */
    public final static String ATTR_RECIPE = StringUtil.toSQLName("mRecipe");

    public Product mProduct;
    public Recipe mRecipe;
    public float mAmount;

    public Ingredient() {
        mProduct = null;
        mRecipe = null;
        mAmount = 1.0f;
    }

    public Ingredient(Product _product, Recipe _recipe, float _amount) {
        mProduct = _product;
        mRecipe = _recipe;
        mAmount = _amount;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) {
            return true;
        }
        if (anotherObject == null || getClass() != anotherObject.getClass()) {
            return false;
        }

        Ingredient anotherIngredient = (Ingredient) anotherObject;

        if (Float.compare(anotherIngredient.mAmount, mAmount) != 0) {
            return false;
        }
        if (!mProduct.equals(anotherIngredient.mProduct)) {
            return false;
        }
        if (!mRecipe.equals(anotherIngredient.mRecipe)) {
            return false;
        }
        return getId().compareTo(anotherIngredient.getId()) == 0;

    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
