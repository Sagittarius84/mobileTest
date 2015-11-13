package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;

/**
 * Represents a product as ingredient in a recipe.
 * Created by michi on 14.04.15.
 */
public class Ingredient extends SugarRecord<Ingredient> {

    public final static String TABLE_NAME = "ingredient";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN {
        public final static String ID = "_id";
        public final static String PRODUCT_ID = "product_id";
        public final static String RECIPE_ID = "recipe_id";
        public final static String AMOUNT = "amount";
        public final static String[] ALL_COLUMNS = {ID, PRODUCT_ID, RECIPE_ID, AMOUNT};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_PREFIXED {
        public final static String ID = TABLE_NAME.concat(COLUMN.ID);
        public final static String PRODUCT_ID = TABLE_NAME.concat(COLUMN.PRODUCT_ID);
        public final static String RECIPE_ID = TABLE_NAME.concat(COLUMN.RECIPE_ID);
        public final static String AMOUNT = TABLE_NAME.concat(COLUMN.AMOUNT);
        public final static String[] ALL_COLUMNS = {ID, PRODUCT_ID, RECIPE_ID, AMOUNT};
    }


    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN.ID + " TEXT PRIMARY KEY,"
            + COLUMN.PRODUCT_ID + " TEXT,"
            + COLUMN.RECIPE_ID + " TEXT,"
            + COLUMN.AMOUNT + " REAL,"
            + "FOREIGN KEY (" + COLUMN.PRODUCT_ID + ") REFERENCES " +  Product.TABLE_NAME + "( " + Product.COLUMN.ID + " )"
            + "ON UPDATE CASCADE ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN.RECIPE_ID + ") REFERENCES " +  Recipe.TABLE_NAME + "( " + Recipe.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + " )"
            + "ON UPDATE CASCADE ON DELETE CASCADE"
            + ")";
    /**
     * @deprecated use {@link COLUMN#ID}
     */
    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");
    /**
     * @deprecated use {@link COLUMN#AMOUNT}
     */
    public final static String ATTR_AMOUNT = StringUtil.toSQLName("mAmount");
    /**
     * @deprecated use {@link COLUMN#RECIPE_ID}
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
