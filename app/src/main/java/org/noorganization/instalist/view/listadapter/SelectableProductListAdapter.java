package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.view.datahandler.SelectedProductDataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinos_000 on 06.05.2015.
 */
public class SelectableProductListAdapter extends ArrayAdapter<Product> {

    final String LOG_TAG    = SelectableProductListAdapter.class.getName();

    private Activity mContext;
    private List<Product> mProducts;
    private List<ListEntry> mSelectableProduct;

    public SelectableProductListAdapter(Activity _Context, List<Product> _ProductList){
        super(_Context, R.layout.list_selectable_product  , _ProductList);

        mContext = _Context;
        mProducts = _ProductList;
        // get saved entries
        mSelectableProduct = SelectedProductDataHandler.getInstance().getListEntries();

        if(mSelectableProduct.size() <= 0) {
            for (Product product : mProducts) {
                ListEntry entry;
                entry = new ListEntry();
                entry.mProduct = product;
                entry.mStruck = false;
                mSelectableProduct.add(entry);
            }
        }
        SelectedProductDataHandler.getInstance().setListEntries(mSelectableProduct);
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;
        ListEntry listEntry      = mSelectableProduct.get(_Position);

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(R.layout.list_selectable_product, null);
        }else{
            view = _ConvertView;
        }

        TextView textView       = (TextView) view.findViewById(R.id.product_list_product_name);
        CheckBox checkBox       = (CheckBox) view.findViewById(R.id.product_list_product_selected);
        textView.setText(listEntry.mProduct.mName);
        checkBox.setChecked(listEntry.mStruck);

        view.setOnClickListener(new OnClickListenerListEntry(listEntry));
        return view;
    }


    private class OnClickListenerListEntry implements View.OnClickListener {

        private ListEntry   mListEntry;
        private CheckBox    mCheckBox;
        public OnClickListenerListEntry(ListEntry listEntry){
            mListEntry = listEntry;
        }
        @Override
        public void onClick(View v) {
            if(mCheckBox == null) {
                mCheckBox = (CheckBox) v.findViewById(R.id.product_list_product_selected);
            }
            mListEntry.mStruck = !mListEntry.mStruck;
            mCheckBox.setChecked(mListEntry.mStruck);

            SelectedProductDataHandler.getInstance().setListEntries(mSelectableProduct);
        }
    }
}
