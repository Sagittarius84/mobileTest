package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Category;

import java.util.List;

/**
 * Created by TS on 21.06.2015.
 */
public class CategoryListAdapter extends ArrayAdapter<Category> {

    private List<Category> mCategoryList;
    private Context       mContext;

    public CategoryListAdapter(Context _Context, List<Category> _CategoryList) {
        super(_Context, android.R.layout.simple_spinner_dropdown_item, _CategoryList);
        mCategoryList = _CategoryList;
        mContext = _Context;
    }

    @Override
    public Category getItem(int _Position) {
        return mCategoryList.get(_Position);
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View     view     = null;
        Category category = mCategoryList.get(_Position);

        if (_ConvertView == null) {
            LayoutInflater categoryNamesInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = categoryNamesInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        } else {
            view = _ConvertView;
        }

        TextView categoryName = (TextView) view.findViewById(android.R.id.text1);
        categoryName.setText(category.mName);
        return view;
    }
}
