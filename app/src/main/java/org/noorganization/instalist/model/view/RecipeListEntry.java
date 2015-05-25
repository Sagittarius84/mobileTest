package org.noorganization.instalist.model.view;

import org.noorganization.instalist.model.Recipe;

/**
 * Created by TS on 25.05.2015.
 */
public class RecipeListEntry extends BaseItemListEntry {

    private Recipe mRecipe;

    public RecipeListEntry(Recipe _Recipe){
        mRecipe = _Recipe;
    }

    @Override
    public String getName() {
        return mRecipe.mName;
    }

    @Override
    public void setName(String _Name) {
        mRecipe.mName = _Name;
    }

    @Override
    public BaseItemReturnType getEntry() {
        return new BaseItemReturnType(mRecipe);
    }

    @Override
    public eItemType getType() {
        return eItemType.RECIPE_LIST_ENTRY;
    }
}
