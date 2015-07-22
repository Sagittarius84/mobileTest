package org.noorganization.instalist.view.modelwrappers;

import org.noorganization.instalist.model.Recipe;

/**
 * The wrapper for Recipes.
 * Created by TS on 25.05.2015.
 */
public class RecipeListEntry implements IBaseListEntry {

    private Recipe mRecipe;
    private boolean mChecked;

    public RecipeListEntry(Recipe _Recipe) {
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
    public eItemType getType() {
        return eItemType.RECIPE_LIST_ENTRY;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean _Checked) {
        mChecked = _Checked;
    }

    @Override
    public Object getItem() {
        return mRecipe;
    }

    @Override
    public long getId() {
        return mRecipe.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeListEntry that = (RecipeListEntry) o;

        return mRecipe.equals(that.mRecipe);

    }

    @Override
    public int hashCode() {
        return mRecipe.hashCode();
    }
}
