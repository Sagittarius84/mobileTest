package org.noorganization.instalist.model.view;

/**
 * Created by TS on 25.05.2015.
 */
public abstract class BaseItemListEntry {

    public enum eItemType{
        PRODUCT_LIST_ENTRY,
        RECIPE_LIST_ENTRY,
        EMPTY,
    }

    public abstract String getName();
    public abstract void setName(String _Name);
    public abstract BaseItemReturnType getEntry();
    public abstract eItemType getType();
}
