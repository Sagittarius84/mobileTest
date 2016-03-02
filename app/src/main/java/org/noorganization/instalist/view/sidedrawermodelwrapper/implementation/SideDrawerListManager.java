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

package org.noorganization.instalist.view.sidedrawermodelwrapper.implementation;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.sidedrawermodelwrapper.helper.implementation.ExpandableShoppingListHelper;
import org.noorganization.instalist.view.sidedrawermodelwrapper.helper.IShoppingListHelper;
import org.noorganization.instalist.view.sidedrawermodelwrapper.ISideDrawerListManager;
import org.noorganization.instalist.view.sidedrawermodelwrapper.helper.implementation.PlainShoppingListHelper;

/**
 * Manager that handles all the overhead to get the right List managed.
 * Created by TS on 26.06.2015.
 */
public class SideDrawerListManager implements ISideDrawerListManager {

    private static String LOG_TAG = SideDrawerListManager.class.getSimpleName();
    //region Private Attributes
    private ExpandableShoppingListHelper mExpandableShoppingListHelper;
    private PlainShoppingListHelper      mPlainShoppingListHelper;

    private IBaseActivity       mBaseActivityInterface;
    private Context             mContext;
    private IShoppingListHelper mShoppingListHelper;
    private ICategoryController mCategoryController;

    /**
     * Flag that indicates, when true, that currently the PlainList is selected,
     * else the ExpandedList is used.
     */
    private boolean mIsPlainList;
    //endregion

    //region Constructor
    public SideDrawerListManager(Activity _Activity, ListView _PlainShoppingListView, ExpandableListView _ExpandableCategoryListView) {
        try {
            mBaseActivityInterface = (IBaseActivity) _Activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Activity.toString() + " has no IBaseActivity interface implemented.");
        }
        mContext = _Activity;
        mCategoryController = ControllerFactory.getCategoryController(mContext);

        long numOfCategories = mCategoryController.getCategoryCount();

        mPlainShoppingListHelper = new PlainShoppingListHelper(mContext, mBaseActivityInterface, _PlainShoppingListView);
        mExpandableShoppingListHelper = new ExpandableShoppingListHelper(mContext, mBaseActivityInterface, _ExpandableCategoryListView);

        // for initializing the views.
        if (numOfCategories > 0) {
            // set Expandable list to viewable
            changeListView(mPlainShoppingListHelper, mExpandableShoppingListHelper);
            mShoppingListHelper = mExpandableShoppingListHelper;
            mIsPlainList = false;
        } else if (numOfCategories == 0) {
            // set plain shoppinglist to viewable
            changeListView(mExpandableShoppingListHelper, mPlainShoppingListHelper);
            mShoppingListHelper = mPlainShoppingListHelper;
            mIsPlainList = true;
        } else {
            throw new IllegalStateException(SideDrawerListManager.class.toString() + " Failed to set ListView type, caused by illegal number of categories");
        }
    }
    //endregion

    //region Public Methods
    @Override
    public void onContextMenuItemClicked(MenuItem _Item) {
        mShoppingListHelper.onContextMenuItemClicked(_Item);
    }

    @Override
    public ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo) {
        return mShoppingListHelper.createContextMenu(_Menu, _View, _MenuInfo);
    }

    @Override
    public void addCategory(Category _Category) {
        mShoppingListHelper.addCategory(_Category);
        checkOfViewChange();
    }

    @Override
    public void updateCategory(Category _Category) {
        mShoppingListHelper.updateCategory(_Category);
    }

    @Override
    public void removeCategory(Category _Category) {
        mShoppingListHelper.removeCategory(_Category);
        Log.i(LOG_TAG, "Removed category.");
        checkOfViewChange();
    }

    @Override
    public void addList(ShoppingList _ShoppingList) {
        mShoppingListHelper.addList(_ShoppingList);
    }

    @Override
    public void updateList(ShoppingList _ShoppingList) {
        mShoppingListHelper.updateList(_ShoppingList);
    }

    @Override
    public void removeList(ShoppingList _ShoppingList) {
        mShoppingListHelper.removeList(_ShoppingList);
    }
    //endregion

    //region Private Methods

    /**
     * Call this when the size of the category has changed.
     */
    private void checkOfViewChange() {

        long                numOfCategories       = mCategoryController.getCategoryCount();
        IShoppingListHelper oldShoppingListHelper = mShoppingListHelper;
        Log.i(LOG_TAG, "Number of categories: " + numOfCategories);
        if (numOfCategories >= 1 && mIsPlainList) {
            mIsPlainList = false;
            // set Expandable list to viewable
            mShoppingListHelper = mExpandableShoppingListHelper;
            changeListView(oldShoppingListHelper, mShoppingListHelper);
            mShoppingListHelper.updateAdapter();
        } else if (numOfCategories == 0 && ! mIsPlainList) {
            mIsPlainList = true;
            // set plain shoppinglist to viewable
            mShoppingListHelper = mPlainShoppingListHelper;
            changeListView(oldShoppingListHelper, mShoppingListHelper);
            mShoppingListHelper.updateAdapter();

        }
    }

    /**
     * Changes the list view with the given helpers.
     *
     * @param _FromHelper the helper class of the from view.
     * @param _ToHelper   the helper class of the to view.
     */
    private void changeListView(IShoppingListHelper _FromHelper, IShoppingListHelper _ToHelper) {
        _FromHelper.setActiveState(false);
        _ToHelper.setActiveState(true);
    }
    //endregion
}
