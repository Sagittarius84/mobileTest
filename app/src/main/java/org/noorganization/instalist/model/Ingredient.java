package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;

/**
 * Represents a product as ingredient in a recipe.
 * Created by michi on 14.04.15.
 */
public class Ingredient extends SugarRecord<Ingredient> {

    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");
    public final static String ATTR_AMOUNT  = StringUtil.toSQLName("mAmount");
    public final static String ATTR_RECIPE  = StringUtil.toSQLName("mRecipe");

    public Product mProduct;
    public Recipe  mRecipe;
    public float   mAmount;

    public Ingredient() {
        mProduct = null;
        mRecipe  = null;
        mAmount  = 1.0f;
    }

    public Ingredient(Product _product, Recipe _recipe, float _amount) {
        mProduct = _product;
        mRecipe  = _recipe;
        mAmount  = _amount;
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
