package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.touchlistener.IOnShoppingListClickListenerEvents;
import org.noorganization.instalist.touchlistener.OnShoppingListClickListener;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.interfaces.ICategoryAdapter;

import java.util.List;

/**
 * Displays Categories and possible lists of these categories.
 * Created by tinos_000 on 16.06.2015.
 */
public class ExpandableCategoryItemListAdapter extends BaseExpandableListAdapter implements ICategoryAdapter {

    private LayoutInflater                     mInflater;
    private List<Category>                     mListOfCategories;
    private IOnShoppingListClickListenerEvents mIOnShoppingListClickEvents;

    private IBaseActivity mBaseAcitvity;

    public ExpandableCategoryItemListAdapter(Context _Context, List<Category> _ListOfCategories) {
        if (_ListOfCategories == null) {
            throw new NullPointerException("Given List of categories cannot be null!");
        }

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
        return mListOfCategories.get(groupPosition).getLists().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListOfCategories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListOfCategories.get(groupPosition).getLists().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mListOfCategories.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mListOfCategories.get(groupPosition).getLists().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroup view;
        Category  category = mListOfCategories.get(groupPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_category_item
        if (convertView != null && convertView.getId() == R.id.expandable_list_view_category_item) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_category, parent, false);
        }

        TextView tvCategoryName      = (TextView) view.findViewById(R.id.expandable_list_view_category_name);
        TextView tvCategoryItemCount = (TextView) view.findViewById(R.id.expandable_list_view_category_entries);
        //ImageView deleteImage        = (ImageView) view.findViewById(R.id.expandable_list_view_edit_delete);

        //deleteImage.setOnClickListener(new OnDeleteCategoryClickListener(category.getId()));
        tvCategoryName.setText(category.mName);
        tvCategoryItemCount.setText(String.valueOf(category.getLists().size()));

        return view;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewGroup    view;
        ShoppingList shoppingList = mListOfCategories.get(groupPosition).getLists().get(childPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_list_item
        if (convertView != null && convertView.getId() == R.id.expandable_list_view_list_item) {
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_list_entry, parent, false);
        }

        view.setLongClickable(true);

        TextView tvListName      = (TextView) view.findViewById(R.id.expandable_list_view_list_name);
        TextView tvListItemCount = (TextView) view.findViewById(R.id.expandable_list_view_list_entries);
        //ImageView deleteImage        = (ImageView) view.findViewById(R.id.expandable_list_view_edit_delete);

        tvListName.setText(shoppingList.mName);
        tvListItemCount.setText(String.valueOf(shoppingList.getEntries().size()));

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
        int indexToUpdate = - 1;
        for (int Index = 0; Index < mListOfCategories.size(); ++ Index) {
            if (mListOfCategories.get(Index).getId() == _Category.getId()) {
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
    public Category findCategoryById(long _Id) {
        Category retCategory = null;
        for (Category category : mListOfCategories) {
            if (_Id == category.getId()) {
                retCategory = category;
                break;
            }
        }
        return retCategory;
    }

    private class OnDeleteCategoryClickListener implements View.OnClickListener {
        private long mCategoryId;

        public OnDeleteCategoryClickListener(long _CategoryId) {
            mCategoryId = _CategoryId;
        }

        @Override
        public void onClick(View v) {
            Category category = findCategoryById(mCategoryId);
            ControllerFactory.getCategoryController().removeCategory(category);
            mBaseAcitvity.removeCategory(category);
            //removeCategory(category);
        }
    }

    private class OnDeleteShoppingListClickListener implements View.OnClickListener {
        private long mShoppingListId;

        public OnDeleteShoppingListClickListener(long _ShoppingListId) {
            mShoppingListId = _ShoppingListId;
        }

        @Override
        public void onClick(View v) {
            ShoppingList shoppingList = ShoppingList.findById(ShoppingList.class, mShoppingListId);
            boolean      deleted      = ControllerFactory.getListController().removeList(shoppingList);
            if (! deleted) {
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.deletion_failed)
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            notifyDataSetChanged();
        }
    }
}
