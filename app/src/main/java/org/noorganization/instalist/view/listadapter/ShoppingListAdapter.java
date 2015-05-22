package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.touchlistener.OnSimpleSwipeGestureListener;

import java.util.List;

/**
 * The Adapter for rendering the shopping list entries to the user.
 * Created by TS on 20.04.2015.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListProductViewHolder> {

    private static String LOG_TAG = ShoppingListAdapter.class.getName();

    private static List<ListEntry> mListOfEntries = null;
    private final Activity mActivity;

    private OnSimpleSwipeGestureListener mOnSimpleSwipeGestureListener;

    // -----------------------------------------------------------

    public final static class ShoppingListProductViewHolder extends RecyclerView.ViewHolder{
        private TextView mProductAmount;
        private TextView mProductName;

        private IListController mListController;
        private ShoppingListProductViewHolder mViewHolderRef;

        private Context mContext;

        public ShoppingListProductViewHolder(View _ItemView, Context _Context) {
            super(_ItemView);

            mProductAmount = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount);
            mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);

            mListController = ControllerFactory.getListController();
            mViewHolderRef = this;
            mContext = _Context;

            _ItemView.setOnTouchListener(new OnSimpleSwipeGestureListener(_ItemView.getContext(), _ItemView) {

                @Override
                public void onSwipeRight(View childView) {
                    super.onSwipeRight(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    mListController.strikeItem(entry);
                    Toast.makeText( mContext, "Item striked: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSwipeLeft(View childView) {
                    super.onSwipeLeft(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    mListController.unstrikeItem(entry);
                    Toast.makeText( mContext, "Item unstriked: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onSingleTap(View childView) {
                    super.onSingleTap(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    Toast.makeText( mContext, "Item selected: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongTap(View childView) {
                    super.onLongTap(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    Toast.makeText(mContext, "Item deleted: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();
                    ControllerFactory.getListController().removeItem(entry);
                }
            });
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
        return new ShoppingListProductViewHolder(view, mActivity);
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
            // element is unstriked
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
     * Removes the given entry from list and notfies the adapter that this object has been removed.
     * @param _Entry the entry of the element that should be deleted.
     */
    public void removeItem(ListEntry _Entry){

        for(ListEntry listEntry : mListOfEntries){

            // somehow only this works for finding the equal ids
            long id1 = _Entry.getId();
            long id2 = listEntry.getId();
            if( id1 == id2){

                int index = mListOfEntries.indexOf(listEntry);
                mListOfEntries.remove(index);
                notifyItemRemoved(index);
            }
        }
        /*
        int position = mListOfEntries.indexOf(_Entry);
        // check if element was removed, if yes so update the specific viewholder.
        if(mListOfEntries.remove(_Entry)){
            notifyItemRemoved(position);
        }*/
    }

    /**
     * Adds the given entry to the list and notifies the adapter to update the view for this element.
     * @param _Entry entry element that should be added.
     */
    public void addItem(ListEntry _Entry){
        mListOfEntries.add(_Entry);
        notifyItemInserted(mListOfEntries.size()-1);
    }

    /**
     * Call to render the given entry in the view.
     * @param _Entry the entry where the display should be updated.
     */
    public void changeItem(ListEntry _Entry){
        // replace entry with changed entry

        for(ListEntry listEntry : mListOfEntries){

            // somehow only this works for finding the equal ids
            long id1 = _Entry.getId();
            long id2 = listEntry.getId();
            if( id1 == id2){

                int index = mListOfEntries.indexOf(listEntry);
                listEntry = _Entry;
                mListOfEntries.set(index, listEntry);
                notifyItemChanged(index);
            }
        }
    }



}
