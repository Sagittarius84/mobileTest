package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.touchlistener.OnSwipeListener;

import java.util.List;

/**
 * Created by TS on 20.04.2015.
 */
public class ShoppingListAdapter extends ArrayAdapter<ListEntry> {

    private static String LOG_TAG = ShoppingListAdapter.class.getName();

    private final List<ListEntry> mListOfEntries;
    private final Activity mContext;

    // -----------------------------------------------------------

    static class ShoppingListProductViewHolder{
        protected TextView mProductAmount;
        protected TextView mProductName;
    }

    // -----------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------

    private View.OnClickListener onProductClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ShoppingListProductViewHolder holder = (ShoppingListProductViewHolder)view.getTag();
            Toast.makeText(view.getContext(), "Selected " + holder.mProductAmount.getText() + " " + holder.mProductName.getText() , Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "Item clicked");
        }
    };


    // -----------------------------------------------------------

    public ShoppingListAdapter(Activity _Context, List<ListEntry> _ListOfEntries){

        super(_Context, R.layout.list_shopping_product_entry, _ListOfEntries);
        this.mContext = _Context;
        this.mListOfEntries = _ListOfEntries;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View shoppingListView = null;

        if( _ConvertView == null)        {

            LayoutInflater shoppingProductInflater = mContext.getLayoutInflater();
            shoppingListView = shoppingProductInflater.inflate(R.layout.list_shopping_product_entry, null);

            final ShoppingListProductViewHolder productViewHolder = new ShoppingListProductViewHolder();
            productViewHolder.mProductAmount    = (TextView) shoppingListView.findViewById(R.id.list_product_shopping_product_amount);
            productViewHolder.mProductName      = (TextView) shoppingListView.findViewById(R.id.list_product_shopping_product_name);
            shoppingListView.setTag(productViewHolder);
        }else{
            shoppingListView = _ConvertView;
        }

        ShoppingListProductViewHolder productViewHolder = (ShoppingListProductViewHolder) shoppingListView.getTag();
        final ListEntry singleEntry = mListOfEntries.get(_Position);

        productViewHolder.mProductAmount.setText(String.valueOf(singleEntry.mAmount));
        productViewHolder.mProductName.setText(String.valueOf(singleEntry.mProduct.mName));

        if(singleEntry.mStruck){
            // element is striked
            productViewHolder.mProductAmount.setPaintFlags(productViewHolder.mProductAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            productViewHolder.mProductName.setPaintFlags(productViewHolder.mProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else{
            // element is unstriked#
            productViewHolder.mProductAmount.setPaintFlags(productViewHolder.mProductAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            productViewHolder.mProductName.setPaintFlags(productViewHolder.mProductName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        shoppingListView.setOnClickListener(onProductClickListener);
        shoppingListView.setOnTouchListener(new OnSwipeListener(this.getContext(), shoppingListView, singleEntry){

            @Override
            public void onSwipeRight(){

                // send event to change current product  to striked through
                this.mEntry.mStruck = true;
                // notify

                Log.i(ShoppingListAdapter.class.getName(), "Strike!");
                ShoppingListProductViewHolder viewHolder = (ShoppingListProductViewHolder) this.mView.getTag();
                viewHolder.mProductAmount.setPaintFlags(viewHolder.mProductAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mProductName.setPaintFlags(viewHolder.mProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                Toast.makeText(getContext(), "Swiped right !", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSwipeLeft(){
                this.mEntry.mStruck = false;

                Log.i(ShoppingListAdapter.class.getName(), "Unstrike!");
                ShoppingListProductViewHolder viewHolder = (ShoppingListProductViewHolder) this.mView.getTag();
                // ~ negates the flag, so all other stuff beside the strike through will be the same
                viewHolder.mProductAmount.setPaintFlags(viewHolder.mProductAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                viewHolder.mProductName.setPaintFlags(viewHolder.mProductName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                Toast.makeText(getContext(), "Swiped Left!", Toast.LENGTH_LONG).show();
            }
        });

        return shoppingListView;
    }

    @Override
    public int getCount() {
        return mListOfEntries.size();
    }
}
