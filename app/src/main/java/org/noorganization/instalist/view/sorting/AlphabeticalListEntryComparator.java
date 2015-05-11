package org.noorganization.instalist.view.sorting;

import org.noorganization.instalist.model.ListEntry;

import java.text.Collator;
import java.util.Comparator;

/**
 * Sorts listentries by products name, alphabetically based on current locale.
 * Created by michi on 05.05.15.
 */
public class AlphabeticalListEntryComparator implements Comparator<ListEntry> {

    @Override
    public int compare(ListEntry _toCheck, ListEntry _baseEntry) {
        return Collator.getInstance().compare(_toCheck.mProduct.mName, _baseEntry.mProduct.mName);
    }

}
