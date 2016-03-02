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

package org.noorganization.instalist.view.event;

import android.support.annotation.NonNull;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Sent when a ShoppingList entry was selected.
 * Created by tinos_000 on 24.11.2015.
 */
public class ShoppingListSelectedMessage {

    /**
     * The attribute ShoppingList.
     */
    public ShoppingList mShoppingList;

    /**
     * Constructor of the EventMessage.
     * @param _shoppingList the shoppingList that was selected.
     */
    public ShoppingListSelectedMessage(@NonNull ShoppingList _shoppingList) {
        mShoppingList = _shoppingList;
    }
}
