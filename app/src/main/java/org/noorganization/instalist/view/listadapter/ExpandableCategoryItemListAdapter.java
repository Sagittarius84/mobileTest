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

package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.interfaces.ICategoryAdapter;
import org.noorganization.instalist.view.touchlistener.IOnShoppingListClickListenerEvents;
import org.noorganization.instalist.view.touchlistener.OnShoppingListClickListener;

import java.util.List;

/**
 * Displays Categories and possible lists of these categories.
 * Created by tinos_000 on 16.06.2015.
 */
public class ExpandableCategoryItemListAdapter extends BaseExpandableListAdapter implements ICategoryAdapter {


    private LayoutInflater mInflater;
    private List<Category> mListOfCategories;
    private IOnShoppingListClickListenerEvents mIOnShoppingListClickEvents;

    private IListController mListController;

    private IBaseActivity mBaseAcitvity;

    public ExpandableCategoryItemListAdapter(Context _Context, List<Category> _ListOfCategories) {
        if (_ListOfCategories == null) {
            throw new NullPointerException("Given List of categories cannot be null!");
        }

        mListController = ControllerFactory.getListController(_Context);
        mInflater = (LayoutInflater) _Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListOfCategories = _ListOfCategories;
        mBaseAcitvity = (IBaseActivity) _Context;
        try {
            mIOnShoppingListClickEvents = (IOnShoppingListClickListenerEvents) _Context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Context.toString()
                    + " has no IOnShoppingListClickListenerEvents interface implemented.");
        }
    }

    @Override
    public int getGroupCount() {
        return mListOfCategories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO weird NullPointerException error, sometimes when deleting a category that is currently opened and has no elements in it, remove this hotfix
        Category category = mListOfCategories.get(groupPosition);
        List<ShoppingList> lists = mListController.getListsByCategory( category.mUUID != null ? category : null);
        return lists == null ? 0 : lists.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListOfCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListController.getListsByCategory(mListOfCategories.get(groupPosition).mUUID != null ? mListOfCategories.get(groupPosition) : null).
                get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return (mListOfCategories.get(groupPosition).mUUID != null) ? mListOfCategories.get(groupPosition).mUUID.hashCode() : 0; // UUID.fromString(mListOfCategories.get(groupPosition).mUUID).getLeastSignificantBits();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mListController.getListsByCategory(mListOfCategories.get(groupPosition).mUUID != null ? mListOfCategories.get(groupPosition) : null)
                .get(childPosition).mUUID.hashCode();
        //UUID.fromString(mListController.getListsByCategory(mListOfCategories.get(groupPosition))
        // .get(childPosition).mUUID).getLeastSignificantBits();
    }


    /**
     * Get the id of the group.
     *
     * @param _groupPosition the position of the group item in the adapter to get the id from.
     * @return the UUID
     */
    public String getGroupUUID(int _groupPosition) {
        return mListOfCategories.get(_groupPosition).mUUID;
    }

    /**
     * Get the id of the child.
     *
     * @param _groupPosition the position of the group item in the adapter to get the id from.
     * @param _childPosition the position of the child item in the adapter to get the id from.
     * @return the UUID
     */
    public String getChildUUID(int _groupPosition, int _childPosition) {
        return mListController.getListsByCategory(mListOfCategories.get(_groupPosition).mUUID != null ? mListOfCategories.get(_groupPosition) : null)
                .get(_childPosition).mUUID;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroup view;
        Category category = mListOfCategories.get(groupPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_category_item
        if (convertView != null && convertView.getId() == R.id.expandable_list_view_category_item) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_category, parent, false);
        }

        TextView tvCategoryName = (TextView) view.findViewById(R.id.expandable_list_view_category_name);
        TextView tvCategoryItemCount = (TextView) view.findViewById(R.id.expandable_list_view_category_entries);

        tvCategoryName.setSelected(true);
        //ImageView deleteImage        = (ImageView) view.findViewById(R.id.expandable_list_view_edit_delete);

        //deleteImage.setOnClickListener(new OnDeleteCategoryClickListener(category.getId()));
        tvCategoryName.setText(category.mName);
        /*tvCategoryItemCount.setText(String.valueOf(mListController.getListsByCategory(mListOfCategories.get(_groupPosition).mUUID != null ? mListOfCategories.get(_groupPosition) : null).
                size()));*/

        return view;
    }


    @Override
    public View getChildView(int _groupPosition, int _childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewGroup view;
        ShoppingList shoppingList = mListController.getListsByCategory(mListOfCategories.get(_groupPosition).mUUID != null ? mListOfCategories.get(_groupPosition) : null).get(_childPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_list_item
        if (convertView != null && convertView.getId() == R.id.expandable_list_view_list_item) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_list_entry, parent, false);
        }

        view.setLongClickable(true);

        TextView tvListName = (TextView) view.findViewById(R.id.expandable_list_view_list_name);
        TextView tvListItemCount = (TextView) view.findViewById(R.id.expandable_list_view_list_entries);
        //ImageView deleteImage        = (ImageView) view.findViewById(R.id.expandable_list_view_edit_delete);

        tvListName.setSelected(true);

        tvListName.setText(shoppingList.mName);
        tvListItemCount.setText(String.valueOf(mListController.getEntryCount(shoppingList)));

        // deleteImage.setOnClickListener(new OnDeleteShoppingListClickListener(shoppingList.getId()));
        view.setOnClickListener(new OnShoppingListClickListener(mIOnShoppingListClickEvents, shoppingList));
        // view.setOnLongClickListener(new OnShoppingListLongClickListener(shoppingList.getId()));
        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // is always selectable because each list can be edited and selected
        return true;
    }

    @Override
    public void addCategory(Category _Category) {
        mListOfCategories.add(_Category);
    }

    @Override
    public void removeCategory(Category _Category) {
        int index = indexOfCategory(_Category);
        if (index < 0) {
            return;
        }
        mListOfCategories.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public void updateCategory(Category _Category) {
        int indexToUpdate = indexOfCategory(_Category);
        if (indexToUpdate < 0) {
            Log.e(ExpandableCategoryItemListAdapter.class.toString(), "No category to update was found.");
            // TODO: some error message or retry for change
            return;
        }
        mListOfCategories.set(indexToUpdate, _Category);
        notifyDataSetChanged();
    }

    public void notifyShoppingListChanged() {
        notifyDataSetChanged();
    }

    /**
     * Searches the index of the given Category in the list of ExpandableCategoryItemListAdapter.
     * It only uses the id of a Category.
     *
     * @param _Category The Category to find.
     * @return -1 if nothing was found, else the index of the given item.
     */
    private int indexOfCategory(Category _Category) {
        // loop through each item to find the desired item, binsearch won't work, because there is no sort list...
        int indexToUpdate = -1;
        for (int Index = 0; Index < mListOfCategories.size(); ++Index) {
            if (mListOfCategories.get(Index).mUUID != null && mListOfCategories.get(Index).mUUID.equals(_Category.mUUID)) {
                indexToUpdate = Index;
                break;
            }
        }
        return indexToUpdate;
    }

    /**
     * Searches for a Category by the given Id.
     *
     * @param _Id Id of the category.
     * @return The category if found, else null.
     */
    public Category findCategoryById(String _Id) {
        Category retCategory = null;
        for (Category category : mListOfCategories) {
            if (_Id.equals(category.mUUID)) {
                retCategory = category;
                break;
            }
        }
        return retCategory;
    }

    // TODO: delete?

    private class OnDeleteCategoryClickListener implements View.OnClickListener {
        private String mCategoryId;

        public OnDeleteCategoryClickListener(String _CategoryId) {
            mCategoryId = _CategoryId;
        }

        @Override
        public void onClick(View v) {
            ICategoryController categoryController = ControllerFactory.getCategoryController(GlobalApplication.getContext());
            Category category = categoryController.getCategoryByID(mCategoryId);
            categoryController.removeCategory(category);
            //BaseAcitvity.removeCategory(category);
            //removeCategory(category);
        }
    }

    // TODO: delete?

    private class OnDeleteShoppingListClickListener implements View.OnClickListener {
        private String mShoppingListId;

        public OnDeleteShoppingListClickListener(String _ShoppingListId) {
            mShoppingListId = _ShoppingListId;
        }

        @Override
        public void onClick(View v) {
            IListController shoppingListController = ControllerFactory.getListController(GlobalApplication.getContext());
            ShoppingList shoppingList = shoppingListController.getListById(mShoppingListId);
            boolean deleted = shoppingListController.removeList(shoppingList);
            if (!deleted) {
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.deletion_failed)
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            notifyDataSetChanged();
        }
    }
}
