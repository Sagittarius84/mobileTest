/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.dataholder;

import org.noorganization.instalist.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data of a recipe currently created.
 * Important to note, this holder tries to not give any null values back.
 * Created by TS on 24.05.2015.
 * @deprecated PLease do not use this any longer.
 */
public class RecipeDataHolder {

    private static RecipeDataHolder mInstance;

    private boolean valuesAreSet;
    /**
     * Save the recipe id
     */
    private long mRecipeID;
    /**
     * Value that identifies if the recipe is new.
     */
    private boolean mIsNew;
    private String mRecipeName;
    private List<Ingredient> mIngredients;

    private List<Ingredient> mRemovedIngredients;

    /**
     * Retrieves an instance of SelectedProductDataHolder.
     *
     * @return instance of SelectedProductDataHolder.
     */
    public static RecipeDataHolder getInstance() {
        if (mInstance == null) {
            mInstance = new RecipeDataHolder();
        }
        return mInstance;
    }

    private RecipeDataHolder(){
        valuesAreSet = false;
    }
    /**
     * Set all the values to save between fragments.
     *
     * @param _RecipeId
     * @param _IsNew
     * @param _RecipeName
     * @param _ListOfIngredients
     */
    public void setValues(long _RecipeId, boolean _IsNew, String _RecipeName, List<Ingredient> _ListOfIngredients, List<Ingredient> _RemovedIngredients) {

        valuesAreSet = true;

        this.mRecipeID = _RecipeId;
        this.mIsNew = _IsNew;
        this.mRecipeName = _RecipeName;
        this.mIngredients = _ListOfIngredients;
        this.mRemovedIngredients = _RemovedIngredients;

    }

    public List<Ingredient> getIngredients() {
        return (mIngredients != null) ? mIngredients : new ArrayList<Ingredient>();
    }

    public String getRecipeName() {
        return this.mRecipeName != null ? this.mRecipeName : "";
    }

    public long getRecipeID() {
        return this.mRecipeID;
    }

    public boolean isNew() {
        return this.mIsNew;
    }

    public void setIngredients(List<Ingredient> _Ingredients) {
        mIngredients = _Ingredients;
    }

    /**
     * Remove all data from this dataholder.
     */
    public void clear() {
        valuesAreSet = false;
        this.mIngredients.clear();
        this.mRemovedIngredients.clear();
        this.mRecipeID = -1L;
        this.mRecipeName = "";
        this.mIsNew = true;
    }

    public List<Ingredient> getRemovedIngredients() {
        return this.mRemovedIngredients;
    }

    public boolean hasSetValues(){
        return valuesAreSet;
    }
}
