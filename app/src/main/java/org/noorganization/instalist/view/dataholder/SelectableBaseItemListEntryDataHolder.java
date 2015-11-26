package org.noorganization.instalist.view.dataholder;

import org.noorganization.instalist.view.modelwrappers.SelectableBaseItemListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 25.05.2015.
 */
public class SelectableBaseItemListEntryDataHolder {


    private static SelectableBaseItemListEntryDataHolder mInstance;

    private List<SelectableBaseItemListEntry> mListEntries;

    /**
     * Retrieves an instance of SelectableBaseItemListEntryDataHolder.
     * @return instance of SelectableBaseItemListEntryDataHolder.
     */
    public static SelectableBaseItemListEntryDataHolder getInstance(){
        if(mInstance == null){
            mInstance = new SelectableBaseItemListEntryDataHolder();
        }
        return mInstance;
    }

    public SelectableBaseItemListEntryDataHolder(){
        this.mListEntries = new ArrayList<>();
    }

    public List<SelectableBaseItemListEntry> getListEntries() {
        return this.mListEntries;
    }

    public void setListEntries(List<SelectableBaseItemListEntry> _ListEntries) {
        this.mListEntries = _ListEntries;
    }

    public void clear(){
        this.mListEntries.clear();
    }
}
