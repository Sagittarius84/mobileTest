package org.noorganization.instalist.view.modelwrappers;

/**
 * // TODO docu!
 * Created by TS on 25.05.2015.
 */
public class SelectableBaseItemListEntry {

    private boolean        mChecked;
    private IBaseListEntry mItemListEntry;

    /**
     * Creates a instance of SelectableBaseItemListEntry, sets the selected field to false.
     * @param _ItemListEntry the entry of the item.
     */
    public SelectableBaseItemListEntry(IBaseListEntry _ItemListEntry){
        mItemListEntry = _ItemListEntry;
        mChecked = false;
    }

    /**
     * Creates a instance of SelectableBaseItemListEntry.
     * @param _ItemListEntry the entry of the item.
     * @param _Checked the information if it is checked or not.
     */
    public SelectableBaseItemListEntry(IBaseListEntry _ItemListEntry, boolean _Checked){
        mItemListEntry = _ItemListEntry;
        mChecked = _Checked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean _Checked) {
        this.mChecked = _Checked;
    }

    public IBaseListEntry getItemListEntry() {
        return mItemListEntry;
    }

    public void setItemListEntry(IBaseListEntry _ItemListEntry) {
        this.mItemListEntry = _ItemListEntry;
    }
}
