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

package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Interface for accessing data of ShoppingList adapter.
 * Created by TS on 26.06.2015.
 */
public interface IShoppingListAdapter {
    /**
     * Adds the given ShoppingList to the adapter.
     * @param _ShoppingList the ShoppingList to be added.
     */
    void addList(ShoppingList _ShoppingList);

    /**
     * Updates the given ShoppingList in Adapter.
     * @param _ShoppingList the ShoppingList to be updated.
     */
    void updateList(ShoppingList _ShoppingList);

    /**
     * Removes the given ShoppingList from the adapter.
     * @param _ShoppingList The ShoppingList to remove.
     */
    void removeList(ShoppingList _ShoppingList);
}
