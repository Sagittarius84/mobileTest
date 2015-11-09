package org.noorganization.instalist.presenter.sorting.selectableList;

import org.noorganization.instalist.presenter.modelwrappers.IBaseListEntry;

import java.text.Collator;
import java.util.Comparator;

/**
 * Sort IBaseListEntries by name.
 * Created by tinos_000 on 23.07.2015.
 */
public class AlphabeticalIBaseListEntryComparator implements Comparator<IBaseListEntry>{
    @Override
    public int compare(IBaseListEntry _toCheck, IBaseListEntry _baseEntry) {
        return Collator.getInstance().compare(_toCheck.getName(), _baseEntry.getName());
    }
}
