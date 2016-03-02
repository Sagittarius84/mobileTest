/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
