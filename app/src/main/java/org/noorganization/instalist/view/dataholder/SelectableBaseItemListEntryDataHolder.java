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

package org.noorganization.instalist.view.dataholder;

import org.noorganization.instalist.view.modelwrappers.SelectableBaseItemListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 25.05.2015.
 * @deprecated PLease do not use this any longer.
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
