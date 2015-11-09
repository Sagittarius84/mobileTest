package org.noorganization.instalist.presenter.interfaces;

import org.noorganization.instalist.presenter.modelwrappers.IBaseListEntry;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Corresponding Interface for data interaction of @see SelectableListAdapter2
 * Created by tinos_000 on 22.07.2015.
 */
public interface ISelectableItemListDataAdapter {

    /**
     * Adds the given IBaseListEntry to the adapter.
     * @param _ListEntry the IBAseListEntry to add.
     */
    void addItem(IBaseListEntry _ListEntry);

    /**
     * Changes the given IBaseListEntry to the adapter.
     * @param _ListEntry the IBAseListEntry to change.
     */
    void changeItem(IBaseListEntry _ListEntry);

    /**
     * Removes the given item from the adapter.
     * @param _ListEntry the IBaseListEntry to remove.
     */
    void removeItem(IBaseListEntry _ListEntry);

    /**
     * Set an comparator so that the entries of this adapter get sort by this comparator.
     * @param _Comparator the comparator to sort by.
     */
    void setComparator(Comparator<IBaseListEntry> _Comparator);

    /**
     * Get the IBaseListEntry at the given position.
     * @param _Position the Position of  the IBaseListEntry item.
     * @return the IBaseListEntry item at the position.
     */
    IBaseListEntry getItem(int _Position);

    /**
     * Returns the iterator of the list of checked entries.
     * @return iterator of List.
     */
    Iterator<IBaseListEntry> getCheckedListEntries();
}
