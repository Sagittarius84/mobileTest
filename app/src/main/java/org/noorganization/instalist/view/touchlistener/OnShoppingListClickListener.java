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

package org.noorganization.instalist.view.touchlistener;

import android.view.View;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Handles the clicks on a ShoppingList item.
 * Created by TS on 20.06.2015.
 */
public class OnShoppingListClickListener implements View.OnClickListener {

    private ShoppingList mShoppingList;
    private IOnShoppingListClickListenerEvents mOnShoppingListClickEvent;

    /**
     * Constructor of OnShoppingListClickListener.
     * @param _IOnShoppingListClickEvent The interface for handling ShoppingList clicks.
     * @param _ShoppingList The ShoppingList that is handled when a click on it occurs.
     */
    public OnShoppingListClickListener(IOnShoppingListClickListenerEvents _IOnShoppingListClickEvent, ShoppingList _ShoppingList){
        mShoppingList = _ShoppingList;
        mOnShoppingListClickEvent = _IOnShoppingListClickEvent;
    }

    @Override
    public void onClick(View v) {
        mOnShoppingListClickEvent.onShoppingListClicked(mShoppingList);
        //EventBus.getDefault().post(new ShoppingListSelectedMessage(mShoppingList));
    }
}
