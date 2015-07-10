package org.noorganization.instalist.model.view;

import org.noorganization.instalist.model.ListEntry;

/**
 * Wrapper for the @Link{ListEntry} for the adapter.
 * Created by TS on 30.06.2015.
 */
public class ListEntryItemWrapper {

    /**
     * Flag that holds the current actionmode of an item. It should not hold multiple actions at once,
     * it can but do not missuse it!
     */
    private int mActionMode;

    /**
     * The possible Action mode codes of ListEntry.
     */
    public final static class ACTION_MODE {

        /**
         * The code that indicates that this entry is in normal mode.
         */
        public final static int NORMAL_MODE = 0x00000001;

        /**
         * The code that indicates that this entry is in edit mode.
         */
        public final static int EDIT_MODE = 0x00000002;

        /**
         * The code that indicates that this entry is in select mode.
         */
        public final static int SELECT_MODE = 0x00000004;
    }

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
        mActionMode = ACTION_MODE.NORMAL_MODE;
    }

    /**
     * Getter for @Link{ListEntry].}
     * @return The @Link{ListEntry}.
     */
    public ListEntry getListEntry() {
        return mListEntry;
    }


    /**
     * Get the current Mode of this ListItem. Therefore see @see{ACTION_MODE}.
     * @return the current Action Mode of this ListItem.
     */
    public int getMode(){
        return mActionMode;
    }
    /**
     * Checks if entity is currently in EditMode.
     * @return true, if in Edit Mode, else false.
     */
    public boolean isEditMode() {
        return mActionMode == ACTION_MODE.EDIT_MODE;
    }

    /**
     * Setter EditMode.
     * @param _EditMode true for EditMode, false for normal Mode.
     */
    public void setEditMode(boolean _EditMode) {
        mActionMode = ACTION_MODE.EDIT_MODE;
    }

    /**
     * Checks if current ListItem is selected.
     * @return true if selected, else false.
     */
    public boolean isSelected() {
        return mActionMode == ACTION_MODE.SELECT_MODE;
    }

    /**
     * Setter for Selected value.
     * @param _Selected true for selected, else false.
     */
    public void setSelected(boolean _Selected) {
        mActionMode = ACTION_MODE.SELECT_MODE;
    }

    public void resetModeToNormalView(){
        mActionMode = ACTION_MODE.NORMAL_MODE;
    }
}
