package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.touchlistener.OnSimpleSwipeGestureListener;
import org.noorganization.instalist.view.customview.AmountPicker;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Adapter for rendering the ShoppingList entries to the user.
 * Created by TS on 20.04.2015.
 */
public class ShoppingItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String LOG_TAG = ShoppingItemListAdapter.class.getName();

    private static List<ListEntry> mListOfEntries = null;
    private final Activity mActivity;

    private OnSimpleSwipeGestureListener mOnSimpleSwipeGestureListener;
    private Comparator                   mComparator;

    // -----------------------------------------------------------

    public final static class ShoppingListProductViewHolder extends RecyclerView.ViewHolder {
        private TextView mProductAmount;
        private TextView mProductType;
        private TextView mProductName;

        private IListController               mListController;
        private ShoppingListProductViewHolder mViewHolderRef;

        private Context mContext;

        public ShoppingListProductViewHolder(View _ItemView, Context _Context) {
            super(_ItemView);

            mProductAmount = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount);
            mProductType = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount_type);
            mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);

            mListController = ControllerFactory.getListController();
            mViewHolderRef = this;
            mContext = _Context;

            mProductAmount.setOnTouchListener(new OnSimpleSwipeGestureListener(_ItemView.getContext(), _ItemView) {
                @Override
                public void onSingleTap(View childView) {
                    super.onSingleTap(childView);
                    childView.findViewById(R.id.list_product_shopping_product_amount);
                }
            });

            _ItemView.setOnTouchListener(new OnSimpleSwipeGestureListener(_ItemView.getContext(), _ItemView) {

                private void toggleStrike(ListEntry _Entry) {
                    if (_Entry.mStruck) {
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
                    Toast.makeText(mContext, "Item selected: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLongTap(View childView) {
                    super.onLongTap(childView);
                    ListEntry entry = mListOfEntries.get(mViewHolderRef.getAdapterPosition());
                    Toast.makeText(mContext, "Item deleted: " + entry.mProduct.mName, Toast.LENGTH_SHORT).show();
                    // ControllerFactory.getListController().removeItem(entry);
                    /*childView.findViewById(R.id.list_product_shopping_product_amount_type_edit);
                    childView.findViewById(R.id.list_product_shopping_product_amount_edit);
                    childView.findViewById(R.id.list_product_shopping_product_name_edit);
                    LinearLayout l1 = (LinearLayout) childView.findViewById(R.id.list_shopping_product_edit_view);
                    LinearLayout l2 = (LinearLayout) childView.findViewById(R.id.list_shopping_product_default_view);

                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.VISIBLE);*/
                }
            });
        }

    }

    public final static class ShoppingListEditProductViewHolder extends RecyclerView.ViewHolder {

        private AmountPicker mProductAmount;
        private Spinner      mProductType;
        // private EditText     mProductName;

        private IListController               mListController;
        private ShoppingListProductViewHolder mViewHolderRef;

        public ShoppingListEditProductViewHolder(View _ItemView, Context _Context) {
            super(_ItemView);
            mProductAmount = (AmountPicker) _ItemView.findViewById(R.id.list_product_shopping_product_amount_edit);
            mProductType = (Spinner) _ItemView.findViewById(R.id.list_product_shopping_product_amount_type_edit);
        }

    }
    // -----------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------

    // -----------------------------------------------------------

    public ShoppingItemListAdapter(Activity _Activity, List<ListEntry> _ListOfEntries) {
        if (_ListOfEntries == null) {
            throw new IllegalArgumentException("List cannot be null!");
        }
        this.mListOfEntries = _ListOfEntries;
        this.mActivity = _Activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup _ViewGroup, int _ViewType) {
        View view;
        switch (_ViewType) {
            case 0:
            default:
                view = LayoutInflater.from(_ViewGroup.getContext()).inflate(R.layout.list_shopping_product_entry, _ViewGroup, false);
                return new ShoppingListProductViewHolder(view, mActivity);
            case 1:
                view = LayoutInflater.from(_ViewGroup.getContext()).inflate(R.layout.list_shopping_product_entry_edit, _ViewGroup, false);
                return new ShoppingListEditProductViewHolder(view, mActivity);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _ProductViewHolder, int _Position) {
        final ListEntry singleEntry = mListOfEntries.get(_Position);
        switch(getItemViewType(_Position)){
            case 0:
            default:
                ShoppingListProductViewHolder viewHolder = (ShoppingListProductViewHolder)_ProductViewHolder;
                TextView productAmount = viewHolder.mProductAmount;
                TextView productName = viewHolder.mProductName;
                TextView productType = viewHolder.mProductType;

                productAmount.setText(ViewUtils.formatFloat(singleEntry.mAmount));
                productName.setText(String.valueOf(singleEntry.mProduct.mName));

                if (singleEntry.mProduct.mUnit != null && singleEntry.mProduct.mUnit.mName.length() > 0) {
                    productType.setText(singleEntry.mProduct.mUnit.mName);
                } else {
                    productType.setText("-");
                }

                if (singleEntry.mStruck) {
                    // element is striked
                    productAmount.setPaintFlags(productAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productType.setPaintFlags(productType.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    productName.setPaintFlags(productName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    // element is unstriked
                    productAmount.setPaintFlags(productAmount.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    productType.setPaintFlags(productType.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    productName.setPaintFlags(productName.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
                break;
            case 1:
                ShoppingListEditProductViewHolder viewHolderEdit = (ShoppingListEditProductViewHolder)_ProductViewHolder;

                AmountPicker prodAmountPicker = viewHolderEdit.mProductAmount;
                Spinner productType1 = viewHolderEdit.mProductType;

                productType1.setAdapter(new ArrayAdapter<Unit>(mActivity, android.R.layout.simple_spinner_item,Unit.listAll(Unit.class)));
                prodAmountPicker.setValue(singleEntry.mAmount);
                break;
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
     *
     * @param _Entry the entry of the element that should be deleted.
     */
    public void removeItem(ListEntry _Entry) {

        int index = - 1;
        synchronized (mListOfEntries) {
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
        if (index >= 0) {
            mListOfEntries.remove(index);
        }
    }

    /**
     * Adds the given entry to the list and notifies the adapter to update the view for this element.
     *
     * @param _Entry entry element that should be added.
     */
    public void addItem(ListEntry _Entry) {
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
     *
     * @param _Entry the entry where the display should be updated.
     */
    public void changeItem(ListEntry _Entry) {
        // replace entry with changed entry
        // TODO performance, usage of some comperator or so...

        int positionToChange = - 1;
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

        if (positionToChange >= 0) {
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
