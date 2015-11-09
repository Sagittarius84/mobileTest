package org.noorganization.instalist.presenter.sorting.shoppingList;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.presenter.modelwrappers.ListEntryItemWrapper;

import java.text.Collator;
import java.util.Comparator;

/**
 * Sorts listentries by products name, alphabetically based on current locale. If possible, use
 * {@link #getInstance()} to retrieve an instance (soft singleton).
 * Created by michi on 05.05.15.
 */
public class PriorityListEntryComparator implements Comparator<ListEntryItemWrapper> {

    private static PriorityListEntryComparator sInstance;

    @Override
    public int compare(ListEntryItemWrapper _toCheck, ListEntryItemWrapper _baseEntry) {

        ListEntry toCheck = _toCheck.getListEntry();
        ListEntry baseEntry = _baseEntry.getListEntry();

        if(toCheck.mStruck && !baseEntry.mStruck){
            return 1;
        }else if(!toCheck.mStruck && baseEntry.mStruck){
            return -1;
        }

        if (toCheck.mPriority > baseEntry.mPriority) {
            return -1;
        }
        if (toCheck.mPriority < baseEntry.mPriority) {
            return 1;
        }
        return Collator.getInstance().compare(toCheck.mProduct.mName, baseEntry.mProduct.mName);
    }

    public static PriorityListEntryComparator getInstance() {
        if (sInstance == null) {
            sInstance = new PriorityListEntryComparator();
        }
        return sInstance;
    }
}
