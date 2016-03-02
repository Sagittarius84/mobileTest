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
