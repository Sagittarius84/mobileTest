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

import android.os.Parcelable;

/**
 * The Interface for accessing Items for the Item overview.
 * Created by TS on 25.05.2015.
 */
public interface IBaseListEntry extends Parcelable {

    enum eItemType{
        PRODUCT_LIST_ENTRY,
        RECIPE_LIST_ENTRY,
        ALL,
        NAME_SEARCH, // no good style

    }

    /**
     * Get the assigned name of the item.
     * @return the name of the item.
     */
    String getName();

    /**
     * Sets the name of the item.
     * @param _Name the name of the item.
     */
    void setName(String _Name);

    /**
     * Get the type of this item.
     * @return
     */
    eItemType getType();

    /**
     * Check if item is checked.
     * @return true if checked, false if not.
     */
    boolean isChecked();

    /**
     * Sets the checked field.
     * @param _Checked true if should be checked, false not to be checked.
     */
    void setChecked(boolean _Checked);

    /**
     * Get the item inside.
     * @return the according item.
     */
    Object getItem();

    /**
     * Get the Id of this item.
     * @return the item of this item.
     */
    String getId();

    /**
     *
     * @param o
     * @return
     */
    boolean equals(Object o);

    /**
     *
     * @return
     */
    int hashCode();
}
