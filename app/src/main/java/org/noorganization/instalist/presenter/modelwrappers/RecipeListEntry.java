package org.noorganization.instalist.presenter.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Recipe;

/**
 * The wrapper for Recipes to strike/unstrike them.
 * Created by TS on 25.05.2015.
 */
public class RecipeListEntry implements IBaseListEntry {

    public static final int CHECKED_PROPERTY = 0;
    private Recipe mRecipe;
    private boolean mChecked;

    public RecipeListEntry(Recipe _Recipe) {
        mRecipe = _Recipe;
    }

    private RecipeListEntry(Parcel _In) {
        mRecipe = ControllerFactory.getRecipeController(GlobalApplication.getContext()).findById(_In.readString());
        mChecked = _In.readSparseBooleanArray().get(CHECKED_PROPERTY);
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
    public String getId() {
        return mRecipe.mUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeListEntry that = (RecipeListEntry) o;
        return mRecipe.mUUID.equals(that.mRecipe.mUUID) && eItemType.RECIPE_LIST_ENTRY == ((IBaseListEntry) o).getType();
    }

    @Override
    public int hashCode() {
        return mRecipe.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel _Dest, int _Flags) {
        SparseBooleanArray booleanArray = new SparseBooleanArray(1);
        booleanArray.append(CHECKED_PROPERTY, mChecked);
        _Dest.writeSparseBooleanArray(booleanArray);
        _Dest.writeString(mRecipe.mUUID);
    }

    public static final Parcelable.Creator<RecipeListEntry> CREATOR = new Parcelable.Creator<RecipeListEntry>() {
        public RecipeListEntry createFromParcel(Parcel _In) {
            return new RecipeListEntry(_In);
        }

        public RecipeListEntry[] newArray(int _Size) {
            return new RecipeListEntry[_Size];
        }
    };
}
