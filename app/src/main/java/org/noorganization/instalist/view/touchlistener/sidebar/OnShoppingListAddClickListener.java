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

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.utils.ViewUtils;

/**
 * Used to process various add list processes to categories.
 * Created by TS on 27.06.2015.
 */
public class OnShoppingListAddClickListener implements View.OnClickListener {

    /**
     * The Category Id.
     */
    private String mCategoryId;

    /**
     * The EditText where the new ShoppingList name is placed.
     */
    private EditText mNewNameEditText;

    private IListController mListController;
    private ICategoryController mCategoryController;

    /**
     * Constructor of OnShoppingListAddClickListener
     * @param _CategoryId the CategoryId where the list should be added. If in PlainList Mode then use the default category.
     */
    public OnShoppingListAddClickListener(Context _context, String _CategoryId, EditText _NewNameEditText) {
        mCategoryId = _CategoryId;
        mNewNameEditText = _NewNameEditText;
        mListController = ControllerFactory.getListController(_context);
        mCategoryController = ControllerFactory.getCategoryController(_context);
    }

    @Override
    public void onClick(View _View) {
        Context context = _View.getContext();

        String listName = ViewUtils.validateAndGetResultEditText(context, mNewNameEditText);
        if (listName == null) {
            return;
        }

        ShoppingList shoppingList = mListController.addList(listName);
        if (shoppingList == null) {
            mNewNameEditText.setError(context.getResources().getString(R.string.list_exists));
            return;
        }
        Category category = mCategoryController.getCategoryByID(mCategoryId);
        shoppingList = mListController.moveToCategory(shoppingList, category);

        if(shoppingList == null){
            mNewNameEditText.setError(context.getResources().getString(R.string.list_to_category_failed));
            return;
        }

        ViewUtils.removeSoftKeyBoard(_View.getContext(), mNewNameEditText);
        _View.setVisibility(View.GONE);
        mNewNameEditText.clearFocus();
        // clear the field
        mNewNameEditText.setText("");
        // ((IBaseActivity) context).addList(shoppingList);

        ((IBaseActivity) context).changeFragment(ShoppingListOverviewFragment.newInstance(shoppingList.mUUID));
    }
}
