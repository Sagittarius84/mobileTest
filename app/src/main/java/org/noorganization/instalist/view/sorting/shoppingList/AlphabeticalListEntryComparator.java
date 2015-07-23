package org.noorganization.instalist.view.sorting.shoppingList;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.view.modelwrappers.ListEntryItemWrapper;

import java.text.Collator;
import java.util.Comparator;

/**
 * Sorts listentries by products name, alphabetically based on current locale. This class is a soft
 * singleton. Since there is no state inside, you should use {@link #getInstance()} to instanciate.
 * Created by michi on 05.05.15.
 */
public class AlphabeticalListEntryComparator implements Comparator<ListEntryItemWrapper> {

    private static AlphabeticalListEntryComparator sInstance;

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

    public static AlphabeticalListEntryComparator getInstance() {
        if (sInstance == null) {
            sInstance = new AlphabeticalListEntryComparator();
        }
        return sInstance;
    }
}
