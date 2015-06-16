package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.interfaces.IBaseActivityListEvent;
import org.noorganization.instalist.view.interfaces.ICategoryListItemAccess;

import java.util.List;

/**
 * Displays Categories and possible lists of these categories.
 * Created by tinos_000 on 16.06.2015.
 */
public class ExpandableCategoryItemListAdapter extends BaseExpandableListAdapter implements ICategoryListItemAccess {

    private LayoutInflater mInflater;
    private List<Category> mListOfCategories;
    private IBaseActivityListEvent mIBaseActivityListEvent;

    public ExpandableCategoryItemListAdapter(Context _Context, List<Category> _ListOfCategories) {

        if(_ListOfCategories == null){
            throw new NullPointerException("Given List of categories cannot be null!");
        }

        mInflater = (LayoutInflater) _Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListOfCategories = _ListOfCategories;
        try {
            mIBaseActivityListEvent = (IBaseActivityListEvent) _Context;
        }catch (ClassCastException e){
            throw new ClassCastException(_Context.toString() + " has no IBaseActivityListEvent implemented.");
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
        Category category = mListOfCategories.get(groupPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_category_item
        if(convertView != null && convertView.getId() == R.id.expandable_list_view_category_item){
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_category, parent, false);
        }

        TextView tvCategoryName = (TextView) view.findViewById(R.id.expandable_list_view_category_name);
        TextView tvCategoryItemCount = (TextView) view.findViewById(R.id.expandable_list_view_category_entries);

        tvCategoryName.setText(category.mName);
        tvCategoryItemCount.setText(String.valueOf(category.getLists().size()));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewGroup view;
        ShoppingList shoppingList = mListOfCategories.get(groupPosition).getLists().get(childPosition);

        // check if the converted view is not null and check if it is already an expandable_list_view_category_item
        if(convertView != null && convertView.getId() == R.id.expandable_list_view_list_item){
            view = (ViewGroup) convertView;
        } else {
            view = (ViewGroup) mInflater.inflate(R.layout.expandable_list_view_list_entry, parent, false);
        }

        TextView tvListName         = (TextView) view.findViewById(R.id.expandable_list_view_list_name);
        TextView tvListItemCount    = (TextView) view.findViewById(R.id.expandable_list_view_list_entries);

        tvListName.setText(shoppingList.mName);
        tvListItemCount.setText(String.valueOf(shoppingList.getEntries().size()));

        view.setOnClickListener(new OnListNameClickListener(shoppingList));

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // is always selectable because each list can be edited
        return true;
    }


    @Override
    public void addCategory(Category _Category) {
        mListOfCategories.add(_Category);
    }

    @Override
    public void removeCategory(Category _Category) {
        mListOfCategories.remove(_Category);
    }

    /**
     * On click listener for managing the on click events on a shoppinglistname
     */
    private class OnListNameClickListener implements View.OnClickListener{

        private ShoppingList mShoppingList;

        public OnListNameClickListener(ShoppingList _ShoppingList){
            mShoppingList = _ShoppingList;
        }
        @Override
        public void onClick(View view) {
            mIBaseActivityListEvent.selectList(mShoppingList);
        }
    }
}
