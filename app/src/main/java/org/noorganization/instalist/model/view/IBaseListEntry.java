package org.noorganization.instalist.model.view;

/**
 * Created by TS on 25.05.2015.
 */
public interface IBaseListEntry {

    enum eItemType{
        PRODUCT_LIST_ENTRY,
        RECIPE_LIST_ENTRY,
        NAME_SEARCH, // no good art
        EMPTY,
    }

    String getName();
    void setName(String _Name);
    BaseItemReturnType getEntry();
    eItemType getType();
}
