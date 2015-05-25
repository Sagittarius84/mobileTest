package org.noorganization.instalist.view.spinneradapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.Product;

import java.util.List;

/**
 * Created by TS on 25.05.2015.
 */
public class ProductSpinnerAdapter extends ArrayAdapter<Product> {

    private final List<Product> mListOfProducts;
    private final Activity mContext;

    public ProductSpinnerAdapter(Activity _Context, List<Product> _ListOfProducts){
        super(_Context, android.R.layout.simple_list_item_1, _ListOfProducts);
        this.mContext = _Context;
        this.mListOfProducts = _ListOfProducts;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
        }else{
            view = _ConvertView;
        }

        String listName     = mListOfProducts.get(_Position).mName;
        TextView textView   = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listName);

        return view;
    }

    @Override
    public Product getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getDropDownView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
        }else{
            view = _ConvertView;
        }

        String listName     = mListOfProducts.get(_Position).mName;
        TextView textView   = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(listName);

        return view;
    }
}
