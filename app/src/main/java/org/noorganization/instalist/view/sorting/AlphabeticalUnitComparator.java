package org.noorganization.instalist.view.sorting;

import org.noorganization.instalist.model.Unit;

import java.text.Collator;
import java.util.Comparator;

/**
 * A comparator for sorting units by name.
 * Created by daMihe on 22.07.2015.
 */
public class AlphabeticalUnitComparator implements Comparator<Unit> {

    private static AlphabeticalUnitComparator sInstance;

    @Override
    public int compare(Unit _lhs, Unit _rhs) {
        if (_lhs.equals(_rhs)) {
            return 0;
        }
        return Collator.getInstance().compare(_lhs.mName, _rhs.mName);
    }

    public static AlphabeticalUnitComparator getInstance() {
        if (sInstance == null) {
            sInstance = new AlphabeticalUnitComparator();
        }
        return sInstance;
    }
}
