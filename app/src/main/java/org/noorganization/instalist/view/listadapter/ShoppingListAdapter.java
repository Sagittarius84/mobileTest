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

import java.util.Collections;
import java.util.Comparator;
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
    private Comparator mComparator;

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

            mProductAmount.setOnTouchListener(new OnSimpleSwipeGestureListener(_ItemView.getContext(), _ItemView ){
                @Override
                public void onSingleTap(View childView) {
                    super.onSingleTap(childView);
                    childView.findViewById(R.id.list_product_shopping_product_amount);
                }
            });

            _ItemView.setOnTouchListener(new OnSimpleSwipeGestureListener(_ItemView.getContext(), _ItemView) {

                private void toggleStrike(ListEntry _Entry){
                    if(_Entry.mStruck){
                        mListController.unstrikeItem(_Entry);
                    } else {
                        mListController.strikeItem(_Entry);
                    }
                }

                @Override
                public void onSwipeRight(View childView) {
                    super.onSwipeRight(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    toggleStrike(entry);
                }

                @Override
                public void onSwipeLeft(View childView) {
                    super.onSwipeLeft(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    toggleStrike(entry);

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

        int index = -1;
        synchronized(mListOfEntries) {
            for (ListEntry listEntry : mListOfEntries) {

                // somehow only this works for finding the equal ids
                long id1 = _Entry.getId();
                long id2 = listEntry.getId();
                if (id1 == id2) {

                    index = mListOfEntries.indexOf(listEntry);
                    notifyItemRemoved(index);
                }
            }
        }
        if(index >= 0) {
            mListOfEntries.remove(index);
        }
    }

    /**
     * Adds the given entry to the list and notifies the adapter to update the view for this element.
     * @param _Entry entry element that should be added.
     */
    public void addItem(ListEntry _Entry){
        mListOfEntries.add(_Entry);
        Collections.sort(mListOfEntries, mComparator);
        notifyDataSetChanged();
    }

    public void sortByComparator(Comparator _Comparator) {
        mComparator = _Comparator;
        // AlphabeticalListEntryComparator comparator = new AlphabeticalListEntryComparator();
        Collections.sort(mListOfEntries, _Comparator);
        notifyDataSetChanged();
    }

    /**
     * Call to render the given entry in the view.
     * @param _Entry the entry where the display should be updated.
     */
    public void changeItem(ListEntry _Entry){
        // replace entry with changed entry
        // TODO performance, usage of some comperator or so...

        int positionToChange = -1;
        synchronized (mListOfEntries) {
            //positionToChange = Collections.binarySearch(mListOfEntries, _Entry, mComparator);

            for (ListEntry listEntry : mListOfEntries) {

                // somehow only this works for finding the equal ids
                long id1 = _Entry.getId();
                long id2 = listEntry.getId();
                if (id1 == id2) {
                    int index = mListOfEntries.indexOf(listEntry);
                    positionToChange = index;
                    // update reference to given entry from controller
                    mListOfEntries.set(index, _Entry);
                    break;
                }
            }
        }

        if(positionToChange >= 0){
            ListEntry entry = mListOfEntries.get(positionToChange);

            /** -6- 3 7 1 **/
            notifyItemChanged(positionToChange);
            Collections.sort(mListOfEntries, mComparator);

            int indexOfMovedEntry = Collections.binarySearch(mListOfEntries, _Entry, mComparator);
            /*
            int posToMoveTo = mListOfEntries.indexOf(_Entry);

            mListOfEntries.remove(positionToChange);
            mListOfEntries.add(posToMoveTo, entry);
*/

            notifyItemMoved(positionToChange, indexOfMovedEntry);
            notifyItemChanged(indexOfMovedEntry);

           // notifyDataSetChanged();
        }
    }
}
