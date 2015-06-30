package org.noorganization.instalist.view.sorting;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.view.ListEntryItemWrapper;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts listentries by products name, alphabetically based on current locale.
 * Created by michi on 05.05.15.
 */
public class AlphabeticalListEntryComparator implements Comparator<ListEntryItemWrapper> {

    @Override
    public int compare(ListEntryItemWrapper _toCheck, ListEntryItemWrapper _baseEntry) {

        ListEntry toCheck = _toCheck.getListEntry();
        ListEntry baseEntry = _baseEntry.getListEntry();

        if(toCheck.mStruck && !baseEntry.mStruck){
            return 1;
        }else if(!toCheck.mStruck && baseEntry.mStruck){
            return -1;
        }
        return Collator.getInstance().compare(toCheck.mProduct.mName, baseEntry.mProduct.mName);
    }

}
