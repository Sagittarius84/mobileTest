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

package org.noorganization.instalist.view.sidedrawermodelwrapper;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import org.noorganization.instalist.view.interfaces.ICategoryAdapter;
import org.noorganization.instalist.view.interfaces.IShoppingListAdapter;

/**
 * The base of the SideDrawerListManager.
 * Created by TS on 26.06.2015.
 */
public interface ISideDrawerListManager extends ICategoryAdapter, IShoppingListAdapter {

    /**
     * Call it from onContextMenuItemClicked of Activity/Fragment.
     * @param _Item The MenuItem to retrieve data.
     */
    void onContextMenuItemClicked(MenuItem _Item);

    /**
     * Extends the given ContextMenu with content.
     * @param _Menu The ContextMenu given by onCreateContextMenu of Activity/Fragment.
     * @param _View The general View given by onCreateContextMenu of Activity/Fragment.
     * @param _MenuInfo The MenuInfo given by onCreateContextMenu of Activity/Fragment.
     * @return
     */
    ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo);
}
