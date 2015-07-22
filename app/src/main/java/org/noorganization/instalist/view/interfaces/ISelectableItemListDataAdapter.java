package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.view.modelwrappers.IBaseListEntry;

import java.util.Iterator;

/**
 * Corresponding Interface for data interaction of @see SelectableListAdapter2
 * Created by tinos_000 on 22.07.2015.
 */
public interface ISelectableItemListDataAdapter {

    void addItem(IBaseListEntry _ListEntry);

    void changeItem(IBaseListEntry _ListEntry);

    void removeItem(IBaseListEntry _ListEntry);

    IBaseListEntry getItem(int _Position);

    /**
     * Returns the iterator of the list of checked entries.
     * @return iterator of List.
     */
    Iterator<IBaseListEntry> getCheckedListEntries();
}
