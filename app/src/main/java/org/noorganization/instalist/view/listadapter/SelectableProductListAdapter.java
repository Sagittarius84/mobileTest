package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.view.MainShoppingListView;

import java.util.List;

/**
 * Created by tinos_000 on 06.05.2015.
 */
public class SelectableProductListAdapter extends ArrayAdapter<Product> {

    final String LOG_TAG    = SelectableProductListAdapter.class.getName();

    private Activity mContext;
    private List<Product> mProducts;

    public SelectableProductListAdapter(Activity _Context, List<Product> _ProductList){
        super(_Context, R.layout.list_selectable_product  , _ProductList);
        mContext = _Context;
        mProducts = _ProductList;
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(R.layout.list_selectable_product, null);
        }else{
            view = _ConvertView;
        }

        Product listName     = mProducts.get(_Position);
        TextView textView   = (TextView) view.findViewById(R.id.product_list_product_name);
        CheckBox checkBox   = (CheckBox) view.findViewById(R.id.product_list_product_selected);
        textView.setText(listName.mName);
        checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox1 = (CheckBox) v.findViewById(R.id.product_list_product_selected);
                checkBox1.setChecked(true);
            }
        });

        //view.setOnClickListener(new OnListNameClickListener(listName));

        return view;
    }


}
