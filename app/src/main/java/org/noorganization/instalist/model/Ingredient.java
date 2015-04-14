package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class Ingredient extends SugarRecord<Ingredient> {

    Product mProduct;
    Recipe  mRecipe;
    float   mAmount;

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

}
