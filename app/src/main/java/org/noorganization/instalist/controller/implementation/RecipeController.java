package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;

public class RecipeController implements IRecipeController {
    private static RecipeController mInstance;

    public static IRecipeController getInstance() {
        if (mInstance == null) {
            mInstance = new RecipeController();
        }
        return mInstance;
    }

    @Override
    public Recipe createRecipe(String _name) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public Recipe renameRecipe(Recipe _toChange, String _newName) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public void removeRecipe(Recipe _toRemove) {
        // TODO This method is a stub.
    }

    @Override
    public Ingredient addOrChangeIngredient(Recipe _recipe, Product _productToAdd, float _amount) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public void removeIngredient(Recipe _recipe, Product _productToRemove) {
        // TODO This method is a stub.
    }

    @Override
    public void removeIngredient(Ingredient _toRemove) {
        // TODO This method is a stub.
    }

    private RecipeController() {
    }
}
