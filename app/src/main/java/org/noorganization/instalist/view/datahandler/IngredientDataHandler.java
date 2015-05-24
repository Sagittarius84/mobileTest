package org.noorganization.instalist.view.datahandler;

import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 24.05.2015.
 */
public class IngredientDataHandler {

    private static IngredientDataHandler mInstance;

    private List<Ingredient> mIngredients;

    /**
     * Retrieves an instance of SelectedProductDataHandler.
     * @return instance of SelectedProductDataHandler.
     */
    public static IngredientDataHandler getInstance(){
        if(mInstance == null){
            mInstance = new IngredientDataHandler();
        }
        return mInstance;
    }

    public List<Ingredient> getIngredients(){
        return (mIngredients != null) ? mIngredients : new ArrayList<Ingredient>();
    }

    public void setIngredients(List<Ingredient> _Ingredients){
        mIngredients = _Ingredients;
    }

}
