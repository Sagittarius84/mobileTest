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

package org.noorganization.instalist.view.sorting.selectableList;

import org.noorganization.instalist.view.modelwrappers.IBaseListEntry;

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
