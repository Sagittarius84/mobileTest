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

package org.noorganization.instalist.view.modelwrappers;

/**
 * // TODO docu!
 * Created by TS on 25.05.2015.
 */
public class SelectableBaseItemListEntry {

    private boolean        mChecked;
    private IBaseListEntry mItemListEntry;

    /**
     * Creates a instance of SelectableBaseItemListEntry, sets the selected field to false.
     * @param _ItemListEntry the entry of the item.
     */
    public SelectableBaseItemListEntry(IBaseListEntry _ItemListEntry){
        mItemListEntry = _ItemListEntry;
        mChecked = false;
    }

    /**
     * Creates a instance of SelectableBaseItemListEntry.
     * @param _ItemListEntry the entry of the item.
     * @param _Checked the information if it is checked or not.
     */
    public SelectableBaseItemListEntry(IBaseListEntry _ItemListEntry, boolean _Checked){
        mItemListEntry = _ItemListEntry;
        mChecked = _Checked;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean _Checked) {
        this.mChecked = _Checked;
    }

    public IBaseListEntry getItemListEntry() {
        return mItemListEntry;
    }

    public void setItemListEntry(IBaseListEntry _ItemListEntry) {
        this.mItemListEntry = _ItemListEntry;
    }
}
