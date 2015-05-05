package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ListEntry;

import java.util.List;

/**
 * The Adapter for rendering the shopping list entries to the user.
 * Created by TS on 20.04.2015.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListProductViewHolder> {

    private static String LOG_TAG = ShoppingListAdapter.class.getName();

    private final List<ListEntry> mListOfEntries;
    private final Activity mActivity;

    // -----------------------------------------------------------

    public final static class ShoppingListProductViewHolder extends RecyclerView.ViewHolder{
        private TextView mProductAmount;
        private TextView mProductName;

        public ShoppingListProductViewHolder(View _ItemView) {
            super(_ItemView);
            mProductAmount = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount);
            mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);
        }


    }

    // -----------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------

    // -----------------------------------------------------------

    public ShoppingListAdapter(Activity _Activity, List<ListEntry> _ListOfEntries){
        if(_ListOfEntries == null){
            throw new IllegalArgumentException("List cannot be null!");
        }
        this.mListOfEntries = _ListOfEntries;
        this.mActivity      = _Activity;
    }


    @Override
    public ShoppingListProductViewHolder onCreateViewHolder(ViewGroup _ViewGroup, int _ViewType){
        View view = LayoutInflater.from(_ViewGroup.getContext()).inflate(R.layout.list_shopping_product_entry, _ViewGroup, false);
        return new ShoppingListProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShoppingListProductViewHolder _ProductViewHolder, int _Position){
        final ListEntry singleEntry = mListOfEntries.get(_Position);

        _ProductViewHolder.mProductAmount.setText(String.valueOf(singleEntry.mAmount));
        _ProductViewHolder.mProductName.setText(String.valueOf(singleEntry.mProduct.mName));

        if(singleEntry.mStruck){
            // element is striked
            _ProductViewHolder.mProductAmount.setPaintFlags(_ProductViewHolder.mProductAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            _ProductViewHolder.mProductName.setPaintFlags(_ProductViewHolder.mProductName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else{
            // element is unstriked#
            _ProductViewHolder.mProductAmount.setPaintFlags(_ProductViewHolder.mProductAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            _ProductViewHolder.mProductName.setPaintFlags(_ProductViewHolder.mProductName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public long getItemId(int position) {
        return mListOfEntries.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mListOfEntries.size();
    }

    /**
     * Removes an item from the given position. Update of the view included.
     * @param position position of the element that should be deleted.
     */
    public void removeItem(int position){
        mListOfEntries.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Adds the given item to the list. Update of the view included.
     * @param entry entry element that should be added.
     */
    public void addItem(ListEntry entry){
        mListOfEntries.add(entry);
        notifyItemInserted(mListOfEntries.size()-1);
    }
}
