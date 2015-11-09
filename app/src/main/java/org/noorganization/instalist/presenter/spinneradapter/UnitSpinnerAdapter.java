package org.noorganization.instalist.presenter.spinneradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Unit;

import java.util.List;

/**
 * Spinner adapter for the Unit Spinner view.
 * Created by tinos_000 on 30.06.2015.
 */
public class UnitSpinnerAdapter extends ArrayAdapter<Unit> {

    private final List<Unit> mListOfUnits;
    private final Context mContext;

    private static class ViewHolder {
        TextView mUnitName;
    }

    public UnitSpinnerAdapter(Context _Context, List<Unit> _ListOfUnits) {
        super(_Context, android.R.layout.simple_list_item_1, _ListOfUnits);
        this.mContext = _Context;
        this.mListOfUnits = _ListOfUnits;
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
        String listName = mListOfUnits.get(_Position).mName;
        viewHolder.mUnitName.setText(listName);
        return view;
    }

    @Override
    public Unit getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getDropDownView(int _Position, View _ConvertView, ViewGroup _Parent) {
        return getView(_Position, _ConvertView, _Parent);
    }
}
