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

import org.noorganization.instalist.model.ListEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 10.05.2015.
 * Holds data of selected product in SelectableProductListAdapter.
 * @deprecated PLease do not use this any longer.
 */
public class SelectedProductDataHolder {

    private static SelectedProductDataHolder mInstance;

    private List<ListEntry> mListEntries;

    /**
     * Retrieves an instance of SelectedProductDataHolder.
     * @return instance of SelectedProductDataHolder.
     */
    public static SelectedProductDataHolder getInstance() {
        if (mInstance == null) {
            mInstance = new SelectedProductDataHolder();
        }
        return mInstance;
    }

    private SelectedProductDataHolder(){
        mListEntries = new ArrayList<ListEntry>();
    }

    public List<ListEntry> getListEntries() {
        return mListEntries;
    }

    public void setListEntries(List<ListEntry> mListEntries) {
        this.mListEntries = mListEntries;
    }

    /**
     * Clear the list entries.
     */
    public void clearListEntries(){
        this.mListEntries = null;
        this.mListEntries = new ArrayList<ListEntry>();
    }
}
