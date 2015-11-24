package org.noorganization.instalist.presenter.interfaces;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.presenter.modelwrappers.ListEntryItemWrapper;

import java.util.Comparator;

/**
 * Created by TS on 09.07.2015.
 */
public interface IShoppingListEntryAction {

    /**
     * Adds the given ListEntry to the adapter.
     * @param _ListEntryId the Id of the ListEntry to add.
     */
    void addListEntry(String _ListEntryId);

    /**
     * Removes the ListEntry corresponding to the given Id.
     * @param _ListEntryId The id of the ListEntry that should be removed.
     */
    void removeListEntry(String _ListEntryId);

    /**
     * Updates the given ListEntry corresponding to the given Id.
     * @param _ListEntry The Id of the ListEntry to update.
     */
    void updateListEntry(ListEntry _ListEntry);

    /**
     * Resets the view back to normal view mode. So one unified view will be displayed.
     */
    void resetEditModeView();

    /**
     * The Entry that was choosed to be edited.
     *
     * @param _Position position of the selected list.
     */
    void setToEditMode(int _Position);

    /**
     * Sorts the entries by the given Comparator.
     * @param _Comparator the Comparator.
     */
    void sortByComparator(Comparator<ListEntryItemWrapper> _Comparator);

    int getPositionForId(String id);
}
