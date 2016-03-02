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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.view.listadapter.ExpandableCategoryItemListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

/**
 * Handle the validation and processing when the submit button of category editing was pressed.
 * Created by TS on 20.06.2015.
 */
public class OnSubmitClickListenerWithParentData implements View.OnClickListener {

    private ViewSwitcher                      mViewSwitcher;
    private EditText                          mNameEditText;
    private String                            mCategoryId;
    private ExpandableCategoryItemListAdapter mAdapter;
    private ICategoryController               mCategoryController;

    public OnSubmitClickListenerWithParentData(Context _context, ViewSwitcher _ViewSwitcher, EditText _NameEditText,
                                               String _CategoryId, ExpandableCategoryItemListAdapter _Adapter) {
        mViewSwitcher = _ViewSwitcher;
        mCategoryId = _CategoryId;
        mNameEditText = _NameEditText;
        mAdapter = _Adapter;
        mCategoryController = ControllerFactory.getCategoryController(_context);
    }

    @Override
    public void onClick(View _View) {
        if (! ViewUtils.checkEditTextIsFilled(mNameEditText)) {
            return;
        }

        String   insertedText = mNameEditText.getText().toString();
        Category oldCategory  = mCategoryController.getCategoryByID(mCategoryId);
        Category newCategory  = mCategoryController.renameCategory(oldCategory, insertedText);
        Context  context      = _View.getContext();

        if (newCategory == null) {
            Toast.makeText(context, _View.getContext().getString(R.string.category_not_found), Toast.LENGTH_SHORT).show();
            return;
        } else if (newCategory.equals(oldCategory)) {
            if (newCategory.mName.compareTo(insertedText) != 0) {
                Toast.makeText(context, context.getString(R.string.category_exists), Toast.LENGTH_SHORT).show();
                mNameEditText.setError(context.getString(R.string.category_exists));
                return;
            }
        }
        ViewUtils.removeSoftKeyBoard(context, mNameEditText);
        mViewSwitcher.showNext();
        // TODO: remove this when callback for this kind is there.
      //   ((IBaseActivity) context).updateCategory(newCategory);


    }
}