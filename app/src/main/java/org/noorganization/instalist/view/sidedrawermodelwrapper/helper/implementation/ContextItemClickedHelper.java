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

package org.noorganization.instalist.view.sidedrawermodelwrapper.helper.implementation;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.touchlistener.sidebar.OnCancelClickListenerWithData;
import org.noorganization.instalist.view.touchlistener.sidebar.OnCancelMoveClickListener;
import org.noorganization.instalist.view.touchlistener.sidebar.OnSubmitClickListenerWithChildData;
import org.noorganization.instalist.view.touchlistener.sidebar.OnSubmitMoveClickListener;
import org.noorganization.instalist.view.sidedrawermodelwrapper.helper.IContextItemClickedHelper;
import org.noorganization.instalist.view.spinneradapter.CategorySpinnerAdapter;

import java.util.List;

/**
 * Helper for when an Item of a context menu of side drawer was clicked.
 * Created by tinos_000 on 25.06.2015.
 */
public class ContextItemClickedHelper implements IContextItemClickedHelper {

    private Context mContext;
    private ICategoryController mCategoryController;
    private IListController     mListController;

    public ContextItemClickedHelper(Context _Context){
        mContext = _Context;
        mCategoryController = ControllerFactory.getCategoryController(mContext);
        mListController = ControllerFactory.getListController(mContext);
    }

    @Override
    public void editListName(View _View, ShoppingList _ShoppingList, ViewSwitcher _ViewSwitcher) {
        ImageView cancelView;
        ImageView submitView;
        EditText editText;
        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

        editText = (EditText) _View.findViewById(R.id.expandable_list_view_list_edit_name);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(_ViewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithChildData(mContext, _ViewSwitcher,
                editText, _ShoppingList.mUUID));

        editText.setText(_ShoppingList.mName);
        _ViewSwitcher.showNext();
    }

    @Override
    public void removeList(ShoppingList _ShoppingList) {
        mListController.removeList(_ShoppingList);
        /*boolean deleted = ControllerFactory.getListController().removeList(_ShoppingList);
        if (!deleted) {
            Toast.makeText(mContext, mContext.getString(R.string.deletion_failed), Toast.LENGTH_SHORT).show();
            return;
        }*/
        //((IBaseActivity) mContext).removeList(_ShoppingList);
    }

    @Override
    public void changeCategoryOfList(View _View, ShoppingList _ShoppingList, Category _CategoryForShoppingList, ViewSwitcher _ViewSwitcher) {
        ImageView cancelView;
        ImageView submitView;
        LinearLayout moveContainer;
        Spinner spinner;
        List<Category> categories;

        moveContainer = (LinearLayout) _View.findViewById(R.id.expandable_list_view_choose_move_category);

        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_move_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_move_submit);
        spinner = (Spinner) _View.findViewById(R.id.expandable_list_view_list_move_spinner);

        categories = mCategoryController.getAllCategories();
        categories.remove(_CategoryForShoppingList);

        SpinnerAdapter spinnerAdapter = new CategorySpinnerAdapter(mContext, categories);
        spinner.setAdapter(spinnerAdapter);

        _ViewSwitcher.setVisibility(View.GONE);
        moveContainer.setVisibility(View.VISIBLE);

        cancelView.setOnClickListener(new OnCancelMoveClickListener(moveContainer, _ViewSwitcher));
        submitView.setOnClickListener(new OnSubmitMoveClickListener(mContext, moveContainer,
                _ViewSwitcher, spinner, _ShoppingList));
    }
}
