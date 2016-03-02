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
 * Interface for registering Fragments for general events.
 * Created by TS on 04.07.2015.
 */
public interface IFragment {

    /**
     * Called when @Link{ShoppingList} has been removed.
     * @param _ShoppingList the @Link{ShoppingList} that has been removed.
     */
    void onShoppingListRemoved(ShoppingList _ShoppingList);
}
