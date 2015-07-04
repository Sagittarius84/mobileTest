package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.model.view.ListEntryItemWrapper;
import org.noorganization.instalist.touchlistener.OnSimpleSwipeGestureListener;
import org.noorganization.instalist.view.customview.AmountPicker;
import org.noorganization.instalist.view.spinneradapter.UnitSpinnerAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Adapter for rendering the ShoppingList entries to the user.
 * Created by TS on 20.04.2015.
 */
public class ShoppingItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int    DEFAULT_VIEW       = 0;
    public static final int    EDIT_MODE_VIEW     = 1;
    public static final int    SELECTED_MODE_VIEW = 2;
    private static      String LOG_TAG            = ShoppingItemListAdapter.class.getName();

    private static List<ListEntryItemWrapper> mListOfEntries = null;
    private final Activity mActivity;

    private OnSimpleSwipeGestureListener mOnSimpleSwipeGestureListener;
    private Comparator                   mComparator;

    /**
     * Indicates that this list is currently in edit mode.
     */
    private static ListEntryItemWrapper mCurrentListInEditMode;

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
        }

    }

    public final static class ShoppingListEditProductViewHolder extends RecyclerView.ViewHolder {

        public AmountPicker mProductAmount;
        public Spinner      mProductType;
        public TextView     mProductName;

        private IListController               mListController;
        private ShoppingListProductViewHolder mViewHolderRef;

        public ShoppingListEditProductViewHolder(View _ItemView, Context _Context) {
            super(_ItemView);
            mProductAmount = (AmountPicker) _ItemView.findViewById(R.id.list_product_shopping_product_amount_edit);
            mProductType = (Spinner) _ItemView.findViewById(R.id.list_product_shopping_product_amount_type_edit);
            mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);
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

        mListOfEntries = new ArrayList<>(_ListOfEntries.size());
        for (ListEntry listEntry : _ListOfEntries) {
            mListOfEntries.add(new ListEntryItemWrapper(listEntry));
        }
        this.mActivity = _Activity;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListOfEntries.get(position).isEditMode()) {
            return EDIT_MODE_VIEW;
        }
        if (mListOfEntries.get(position).isSelected()) {
            return SELECTED_MODE_VIEW;
        }

        // if we are in normal mode, return 0
        return DEFAULT_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup _ViewGroup, int _ViewType) {
        View view;
        switch (_ViewType) {
            case DEFAULT_VIEW:
            default:
                view = LayoutInflater.from(_ViewGroup.getContext()).inflate(R.layout.list_shopping_product_entry, _ViewGroup, false);
                return new ShoppingListProductViewHolder(view, mActivity);
            case EDIT_MODE_VIEW:
                view = LayoutInflater.from(_ViewGroup.getContext()).inflate(R.layout.list_shopping_product_entry_edit, _ViewGroup, false);
                return new ShoppingListEditProductViewHolder(view, mActivity);
            /*
            TODO: Implement selectable list
            case SELECTED_MODE_VIEW:
                return ;*/
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _ProductViewHolder, int _Position) {
        final ListEntryItemWrapper singleEntryItemWrapper = mListOfEntries.get(_Position);
        final ListEntry            singleEntry            = singleEntryItemWrapper.getListEntry();

        switch (getItemViewType(_Position)) {
            case DEFAULT_VIEW:
            default:
                ShoppingListProductViewHolder viewHolder = (ShoppingListProductViewHolder) _ProductViewHolder;
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
            case EDIT_MODE_VIEW:
                ShoppingListEditProductViewHolder viewHolderEdit = (ShoppingListEditProductViewHolder) _ProductViewHolder;

                AmountPicker prodAmountPicker = viewHolderEdit.mProductAmount;
                Spinner productType1 = viewHolderEdit.mProductType;
                TextView productName1 = viewHolderEdit.mProductName;
                productName1.setText(singleEntry.mProduct.mName);
                productType1.setAdapter(new UnitSpinnerAdapter(mActivity, Unit.listAll(Unit.class)));
                prodAmountPicker.setValue(singleEntry.mAmount);
                break;
            case SELECTED_MODE_VIEW:
                // TODO: do some stuff :D
                break;
        }

    }

    @Override
    public long getItemId(int position) {
        return mListOfEntries.get(position).getListEntry().getId();
    }

    @Override
    public int getItemCount() {
        return mListOfEntries.size();
    }

    /**
     * Get the position of the given id.
     *
     * @param id the id to find.
     * @return -1 if nothing was found, the index if found.
     */
    public synchronized int getPositionForId(long id) {
        int position = - 1;
        for (ListEntryItemWrapper listItem : mListOfEntries) {
            if (listItem.getListEntry().getId().equals(id)) {
                position = mListOfEntries.indexOf(listItem);
                break;
            }
        }
        return position;
    }

    /**
     * Removes the given entry from list and notfies the adapter that this object has been removed.
     *
     * @param _Entry the entry of the element that should be deleted.
     */
    public void removeItem(ListEntry _Entry) {

        int                  index            = - 1;
        ListEntryItemWrapper entryItemWrapper = new ListEntryItemWrapper(_Entry);

        index = getPositionForId(_Entry.getId());
        if (index >= 0) {
            mListOfEntries.remove(index);
            notifyItemRemoved(index);
        }

    }

    private void resetEditModeViewInternal() {
        if (mCurrentListInEditMode != null) {
            mCurrentListInEditMode.setEditMode(false);
            notifyItemChanged(mListOfEntries.indexOf(mCurrentListInEditMode));
        }
    }

    /**
     * Resets the view back to normal view mode. So one unified view will be displayed.
     */
    public void resetEditModeView() {
        resetEditModeViewInternal();
    }

    /**
     * The Entry that was choosed to be edited.
     *
     * @param _Position position of the selected list.
     */
    public void setToEditMode(int _Position) {
        resetEditModeViewInternal();

        mCurrentListInEditMode = mListOfEntries.get(_Position);
        mCurrentListInEditMode.setEditMode(true);
        notifyItemChanged(_Position);
    }

    /**
     * Adds the given entry to the list and notifies the adapter to update the view for this element.
     *
     * @param _Entry entry element that should be added.
     */
    public void addItem(ListEntry _Entry) {
        // to prevent the same kind of entries in this list. Not nice but it works.
        if (getPositionForId(_Entry.getId()) >= 0) {
            return;
        }

        mListOfEntries.add(new ListEntryItemWrapper(_Entry));
        Collections.sort(mListOfEntries, mComparator);
        notifyDataSetChanged();
    }

    public void sortByComparator(Comparator<ListEntryItemWrapper> _Comparator) {
        mComparator = _Comparator;
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

        int                  positionToChange     = - 1;
        ListEntryItemWrapper listEntryItemWrapper = new ListEntryItemWrapper(_Entry);

        positionToChange = getPositionForId(_Entry.getId());
        synchronized (mListOfEntries) {
            mListOfEntries.set(positionToChange, listEntryItemWrapper);
        }

        if (positionToChange >= 0) {
            /** -6- 3 7 1 **/
            notifyItemChanged(positionToChange);
            Collections.sort(mListOfEntries, mComparator);

            int indexOfMovedEntry = Collections.binarySearch(mListOfEntries, listEntryItemWrapper, mComparator);
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
