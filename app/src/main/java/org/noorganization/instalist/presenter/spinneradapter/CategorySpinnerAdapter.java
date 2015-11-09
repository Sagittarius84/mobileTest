package org.noorganization.instalist.presenter.spinneradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Category;

import java.util.List;

/**
 * Spinner adapter for the Unit Spinner view.
 * Created by ts on 06.07.2015.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private final List<Category> mListOfCategories;
    private final Context        mContext;

    private static class ViewHolder {
        TextView mUnitName;
    }

    public CategorySpinnerAdapter(Context _Context, List<Category> _ListOfCategories) {
        super(_Context, android.R.layout.simple_list_item_1, _ListOfCategories);
        this.mContext = _Context;
        this.mListOfCategories = _ListOfCategories;
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if (_ConvertView == null) {
            ViewHolder viewHolder = new ViewHolder();
            LayoutInflater shoppingListNamesInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
            viewHolder.mUnitName = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(viewHolder);
        } else {
            view = _ConvertView;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String     listName   = mListOfCategories.get(_Position).mName;
        viewHolder.mUnitName.setText(listName);
        return view;
    }

    @Override
    public Category getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getDropDownView(int _Position, View _ConvertView, ViewGroup _Parent) {
        return getView(_Position, _ConvertView, _Parent);
    }
}

