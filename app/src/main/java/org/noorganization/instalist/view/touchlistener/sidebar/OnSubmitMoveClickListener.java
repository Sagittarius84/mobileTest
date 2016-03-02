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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.interfaces.IBaseActivity;

/**
 * OnSubmitMoveClickListener handles the move action when a list should be moved to another category.
 * Created by TS on 21.06.2015.
 */
public class OnSubmitMoveClickListener implements View.OnClickListener {
    private LinearLayout mMoveCategoryLayout;
    private ViewSwitcher mMainView;
    private Spinner mSpinner;
    private ShoppingList mShoppingList;
    private IListController mListController;

    /**
     * Constructor of OnSubmitMoveClickListener.
     * @param _MoveCategoryLayout The container of the view where the changeaction is hold.
     * @param _MainView The container of the ViewSwitcher where the default view is defined.
     * @param _Spinner The Spinner object where a category can be cohoosen.
     * @param _ShoppingList  The ShoppingList that should be moved.
     */
    public OnSubmitMoveClickListener( Context _context, LinearLayout _MoveCategoryLayout,
                                      ViewSwitcher _MainView, Spinner _Spinner,
                                      ShoppingList _ShoppingList){
        mMoveCategoryLayout = _MoveCategoryLayout;
        mMainView = _MainView;
        mSpinner = _Spinner;
        mShoppingList = _ShoppingList;
        mListController = ControllerFactory.getListController(_context);
    }

    @Override
    public void onClick(View _View) {
        Context context = _View.getContext();

        Category category = (Category) mSpinner.getSelectedItem();
        Category oldCategory = mShoppingList.mCategory;
        mShoppingList = mListController.moveToCategory(mShoppingList, category);
        if(mShoppingList == null){
            Toast.makeText(context, context.getString(R.string.change_of_category_of_list_failed), Toast.LENGTH_SHORT).show();
        }
        // TODO: remove this when callback for this kind is there.
        // IBaseActivity baseActivity = ((IBaseActivity) context);
        if(oldCategory != null){
            //baseActivity.updateCategory(oldCategory);
        }
        //baseActivity.updateCategory(category);
        // ENDTODO
        mMoveCategoryLayout.setVisibility(View.GONE);
        mMainView.setVisibility(View.VISIBLE);

    }
}
