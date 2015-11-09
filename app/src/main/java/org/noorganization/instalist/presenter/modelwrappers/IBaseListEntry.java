package org.noorganization.instalist.presenter.modelwrappers;

import android.os.Parcelable;

/**
 * The Interface for accessing Items for the Item overview.
 * Created by TS on 25.05.2015.
 */
public interface IBaseListEntry extends Parcelable {

    enum eItemType{
        PRODUCT_LIST_ENTRY,
        RECIPE_LIST_ENTRY,
        ALL,
        NAME_SEARCH, // no good style

    }

    /**
     * Get the assigned name of the item.
     * @return the name of the item.
     */
    String getName();

    /**
     * Sets the name of the item.
     * @param _Name the name of the item.
     */
    void setName(String _Name);

    /**
     * Get the type of this item.
     * @return
     */
    eItemType getType();

    /**
     * Check if item is checked.
     * @return true if checked, false if not.
     */
    boolean isChecked();

    /**
     * Sets the checked field.
     * @param _Checked true if should be checked, false not to be checked.
     */
    void setChecked(boolean _Checked);

    /**
     * Get the item inside.
     * @return the according item.
     */
    Object getItem();

    /**
     * Get the Id of this item.
     * @return the item of this item.
     */
    long getId();

    /**
     *
     * @param o
     * @return
     */
    boolean equals(Object o);

    /**
     *
     * @return
     */
    int hashCode();
}
