package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
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
        if (_name == null) {
            return null;
        }
        if(Select.from(Recipe.class).where(Condition.prop("m_name").eq(_name)).count() != 0) {
            return null;
        }

        Recipe rtn = new Recipe(_name);
        rtn.save();

        return rtn;
    }

    @Override
    public Recipe renameRecipe(Recipe _toChange, String _newName) {
        if (_newName == null || _toChange == null) {
            return null;
        }
        Recipe toRename = SugarRecord.findById(Recipe.class, _toChange.getId());
        if (toRename == null) {
            return null;
        }

        for (Recipe toCheck : Select.from(Recipe.class).
                where(Condition.prop("m_name").eq(_newName)).list()) {
            if (toCheck.getId().compareTo(toRename.getId()) != 0) {
                return toRename;
            }
        }

        toRename.mName = _newName;
        toRename.save();

        return toRename;
    }

    @Override
    public void removeRecipe(Recipe _toRemove) {
        if (_toRemove == null) {
            return;
        }

        Recipe toDelete = SugarRecord.findById(Recipe.class, _toRemove.getId());
        if (toDelete != null) {
            SugarRecord.deleteAll(Ingredient.class, "m_recipe = ?", _toRemove.getId() + "");
            toDelete.delete();
        }
    }

    @Override
    public Ingredient addOrChangeIngredient(Recipe _recipe, Product _productToAdd, float _amount) {
        if (_recipe == null || _productToAdd == null) {
            return null;
        }

        Recipe savedRecipe = SugarRecord.findById(Recipe.class, _recipe.getId());
        Product savedProduct = SugarRecord.findById(Product.class, _productToAdd.getId());
        if (savedProduct == null || savedRecipe == null) {
            return null;
        }
        Ingredient toCreateOrChange = Select.from(Ingredient.class).where(
                Condition.prop("m_product").eq(savedProduct.getId()),
                Condition.prop("m_recipe").eq(savedRecipe.getId())).first();
        if (_amount < 0.001f) {
            return toCreateOrChange;
        }
        if (toCreateOrChange == null) {
            toCreateOrChange = new Ingredient(savedProduct, savedRecipe, _amount);
        } else {
            toCreateOrChange.mAmount = _amount;
        }
        toCreateOrChange.save();

        return toCreateOrChange;
    }

    @Override
    public void removeIngredient(Recipe _recipe, Product _productToRemove) {
        if (_recipe == null || _productToRemove == null) {
            return;
        }

        Ingredient toRemove = Select.from(Ingredient.class).where(
                Condition.prop("m_recipe").eq(_recipe.getId()),
                Condition.prop("m_product").eq(_productToRemove.getId())).first();
        if (toRemove != null) {
            toRemove.delete();
        }
    }

    @Override
    public void removeIngredient(Ingredient _toRemove) {
        if (_toRemove == null) {
            return;
        }
        Ingredient toRemove = _toRemove;
        toRemove = SugarRecord.findById(Ingredient.class, _toRemove.getId());
        if (toRemove == null) {
            return;
        }
        toRemove.delete();
    }

    private RecipeController() {
    }
}
