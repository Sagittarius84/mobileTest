package org.noorganization.instalist.view.datahandler;

import org.noorganization.instalist.model.ListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 10.05.2015.
 * Holds data of selected product in SelectableProductListAdapter.
 */
public class SelectedProductDataHandler {

    private static SelectedProductDataHandler mInstance;

    private List<ListEntry> mListEntries;

    /**
     * Retrieves an instance of SelectedProductDataHandler.
     * @return instance of SelectedProductDataHandler.
     */
    public static SelectedProductDataHandler getInstance(){
        if(mInstance == null){
            mInstance = new SelectedProductDataHandler();
        }
        return mInstance;
    }

    private SelectedProductDataHandler(){
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
