package org.noorganization.instalist.model.view;

import org.noorganization.instalist.model.ListEntry;

/**
 * Wrapper for the @Link{ListEntry} for the adapter.
 * Created by TS on 30.06.2015.
 */
public class ListEntryItemWrapper {

    /**
     * Reference to the @Link{ListEntry]
     */
    private ListEntry mListEntry;

    /**
     * Flag if @Link{ListEntry} is selected.
     */
    private boolean mEditMode;

    /**
     * Flag if current @Link{ListEntry}
     */
    private boolean mSelected;


    public ListEntryItemWrapper(ListEntry _ListEntry){
        mListEntry = _ListEntry;
        mEditMode = false;
        mSelected = false;
    }

    /**
     * Getter for @Link{ListEntry].}
     * @return The @Link{ListEntry}.
     */
    public ListEntry getListEntry() {
        return mListEntry;
    }

    /**
     * Checks if entity is currently in EditMode.
     * @return true, if in Edit Mode, else false.
     */
    public boolean isEditMode() {
        return mEditMode;
    }

    /**
     * Setter EditMode.
     * @param mEditMode true for EditMode, false for normal Mode.
     */
    public void setEditMode(boolean mEditMode) {
        this.mEditMode = mEditMode;
    }

    /**
     * Checks if current ListItem is selected.
     * @return true if selected, else false.
     */
    public boolean isSelected() {
        return mSelected;
    }

    /**
     * Setter for Selected value.
     * @param mSelected true for selected, else false.
     */
    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
