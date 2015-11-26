package org.noorganization.instalist.view.dataholder;

import org.noorganization.instalist.model.ListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 10.05.2015.
 * Holds data of selected product in SelectableProductListAdapter.
 */
public class SelectedProductDataHolder {

    private static SelectedProductDataHolder mInstance;

    private List<ListEntry> mListEntries;

    /**
     * Retrieves an instance of SelectedProductDataHolder.
     * @return instance of SelectedProductDataHolder.
     */
    public static SelectedProductDataHolder getInstance() {
        if (mInstance == null) {
            mInstance = new SelectedProductDataHolder();
        }
        return mInstance;
    }

    private SelectedProductDataHolder(){
        mListEntries = new ArrayList<ListEntry>();
    }

    public List<ListEntry> getListEntries() {
        return mListEntries;
    }

    public void setListEntries(List<ListEntry> mListEntries) {
        this.mListEntries = mListEntries;
    }

    /**
     * Clear the list entries.
     */
    public void clearListEntries(){
        this.mListEntries = null;
        this.mListEntries = new ArrayList<ListEntry>();
    }
}
