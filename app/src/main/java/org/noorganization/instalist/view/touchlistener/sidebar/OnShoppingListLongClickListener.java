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

package org.noorganization.instalist.view.touchlistener.sidebar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.ShoppingList;

// TODO: delete?

/**
 * Handles the viewchange from textview to edittext.
 */
public class OnShoppingListLongClickListener implements View.OnLongClickListener {

    private String mShoppingListId;

    public OnShoppingListLongClickListener(String _ShoppingListId) {
        mShoppingListId = _ShoppingListId;
    }

    @Override
    public boolean onLongClick(View _View) {

        EditText     editText;
        ShoppingList shoppingList;
        ViewSwitcher viewSwitcher;
        ImageView    cancelView, submitView;

        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

        viewSwitcher = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
        editText = (EditText) _View.findViewById(R.id.expandable_list_view_list_edit_name);

        shoppingList = ControllerFactory.getListController(GlobalApplication.getContext()).getListById(mShoppingListId);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithChildData(GlobalApplication.getContext(),viewSwitcher, editText, mShoppingListId));

        editText.setText(shoppingList.mName);
        viewSwitcher.showNext();
        return false;
    }
}