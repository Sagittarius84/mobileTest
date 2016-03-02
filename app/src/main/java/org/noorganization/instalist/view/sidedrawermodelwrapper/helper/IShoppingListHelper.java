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

package org.noorganization.instalist.view.sidedrawermodelwrapper.helper;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import org.noorganization.instalist.view.interfaces.ICategoryAdapter;
import org.noorganization.instalist.view.interfaces.IShoppingListAdapter;

/**
 * General interface to communicate with the single helper classes.
 * Created by tinos_000 on 25.06.2015.
 */
public interface IShoppingListHelper extends IShoppingListAdapter, ICategoryAdapter{

    /**
     * Called to create a context menu relating to the current list.
     *
     * @param _Menu     the context menu where the menu items should be added.
     * @param _View     the given View by onCreateContextMenu.
     * @param _MenuInfo the given MenuInfo given by onCreteContextMenu.
     * @return the extended ContextMenu.
     */
    ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo);

    void onContextMenuItemClicked(MenuItem _Item);

    /**
     * Checks if the current ListRenderer is active.
     *
     * @return true if active, false if inactive.
     */
    boolean isActive();

    /**
     * Set the List that should be rendered to true, the other lists to false. Also sets the visibility to visible or gone.
     *
     * @param _IsActive true if the ShoppingList should be rendered else it is not.
     */
    void setActiveState(boolean _IsActive);

    /**
     * Used to update the underlying adapterdata to the current state. Used to prevent the recreation of the helper strucutre.
     */
    void updateAdapter();

}
