package org.noorganization.instalist.presenter.listadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.presenter.customview.AmountPicker;
import org.noorganization.instalist.presenter.interfaces.IShoppingListEntryAction;
import org.noorganization.instalist.presenter.modelwrappers.ListEntryItemWrapper;
import org.noorganization.instalist.presenter.sorting.shoppingList.AlphabeticalListEntryComparator;
import org.noorganization.instalist.presenter.spinneradapter.UnitSpinnerAdapter;
import org.noorganization.instalist.presenter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The ShoppingItemListAdapter that handles the display of data.
 * It also includes the management of dynamic contents, means that data can be inserted at
 * runtime without reloading the whole adapter.
 * Created by TS on 09.07.2015.
 */
public class ShoppingItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IShoppingListEntryAction {

    //region private attributes
    private static String LOG_TAG = ShoppingItemListAdapter.class.getName();

    /**
     * The list of Entries.
     */
    private List<ListEntryItemWrapper> mListOfEntries = null;

    /**
     * The context of the app.
     */
    private Context mContext;

    /**
     * The current comparator that is assigned for sorting of the data in mListOfEntries.
     */
    private Comparator<ListEntryItemWrapper> mCurrentComparator;

    /**
     * The current ListEntry that is in edit mode. Used to shorten acceess time.
     */
    private ListEntryItemWrapper mCurrentItemInEditMode;

    //endregion

    //region Constructor

    /**
     * Constructor of ShoppingItemListAdapter2.
     * Uses by default the Alphabetical comparator.
     *
     * @param _Context       The context of the app.
     * @param _ListOfEntries the array of ListEntries that should be used.
     */
    public ShoppingItemListAdapter(Context _Context, List<ListEntry> _ListOfEntries) {
        if (_ListOfEntries == null) {
            throw new IllegalArgumentException("List cannot be null!");
        }

        mListOfEntries = new ArrayList<>(_ListOfEntries.size());
        for (ListEntry listEntry : _ListOfEntries) {
            mListOfEntries.add(new ListEntryItemWrapper(listEntry));
        }

        mContext = _Context;
        mCurrentComparator = new AlphabeticalListEntryComparator();
    }
    //endregion

    public static class ViewType {
        public static final int NORMAL_VIEW        = 0;
        public static final int EDIT_MODE_VIEW     = 1;
        public static final int SELECTED_MODE_VIEW = 2;
    }

    // -----------------------------------------------------------

    //region private Viewholder
    private final static class ViewHolder {
        public final static class ShoppingListProductViewHolder extends RecyclerView.ViewHolder {
            private TextView mProductAmount;
            private TextView mProductType;
            private TextView mProductName;

            //private Context mContext;

            public ShoppingListProductViewHolder(View _ItemView, Context _Context) {
                super(_ItemView);

                mProductAmount = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount);
                mProductType = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_amount_type);
                mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);

                // mContext = _Context;
            }

            public void init(ListEntry _SingleEntry) {

                mProductAmount.setText(ViewUtils.formatFloat(_SingleEntry.mAmount));
                mProductName.setText(String.valueOf(_SingleEntry.mProduct.mName));

                if (_SingleEntry.mProduct.mUnit != null && _SingleEntry.mProduct.mUnit.mName.length() > 0) {
                    mProductType.setText(_SingleEntry.mProduct.mUnit.mName);
                } else {
                    mProductType.setText("");
                }
                List<TextView> textViewsToStroke = new ArrayList<>();
                textViewsToStroke.add(mProductAmount);
                textViewsToStroke.add(mProductName);
                textViewsToStroke.add(mProductType);
                ViewUtils.setStrokeView(_SingleEntry.mStruck, textViewsToStroke);
            }

        }

        public final static class ShoppingListEditProductViewHolder extends RecyclerView.ViewHolder {

            public AmountPicker mProductAmount;
            public Spinner      mProductType;
            public TextView     mProductName;

            private Context mContext;
            private IUnitController mUnitController;

            public ShoppingListEditProductViewHolder(View _ItemView, Context _Context) {
                super(_ItemView);
                mContext = _Context;
                mProductAmount = (AmountPicker) _ItemView.findViewById(R.id.list_product_shopping_product_amount_edit);
                mProductType = (Spinner) _ItemView.findViewById(R.id.list_product_shopping_product_amount_type_edit);
                mProductName = (TextView) _ItemView.findViewById(R.id.list_product_shopping_product_name);
                mUnitController = ControllerFactory.getUnitController(mContext);
            }

            public void init(ListEntry _SingleEntry) {
                mProductName.setText(_SingleEntry.mProduct.mName);
                mProductType.setAdapter(new UnitSpinnerAdapter(mContext, mUnitController.
                        listAll(null, false)));
                mProductAmount.setValue(_SingleEntry.mAmount);
            }

        }
    }
    //endregion

    @Override
    public int getItemViewType(int _Position) {
        switch (mListOfEntries.get(_Position).getMode()) {
            default:
            case ListEntryItemWrapper.ACTION_MODE.NORMAL_MODE:
                return ViewType.NORMAL_VIEW;
            case ListEntryItemWrapper.ACTION_MODE.EDIT_MODE:
                return ViewType.EDIT_MODE_VIEW;
            case ListEntryItemWrapper.ACTION_MODE.SELECT_MODE:
                return ViewType.SELECTED_MODE_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup _Parent, int _ViewType) {

        View           view;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (_ViewType) {
            case ViewType.NORMAL_VIEW:
                view = layoutInflater.inflate(R.layout.list_shopping_product_entry, _Parent, false);
                return new ViewHolder.ShoppingListProductViewHolder(view, mContext);
            case ViewType.EDIT_MODE_VIEW:
                view = layoutInflater.inflate(R.layout.list_shopping_product_entry_edit, _Parent, false);
                return new ViewHolder.ShoppingListEditProductViewHolder(view, mContext);
            case ViewType.SELECTED_MODE_VIEW:
                // TODO: implement! break;
            default:
                throw new IllegalStateException("Type: " + _ViewType + " is not defined as Viewtype.");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _ProductViewHolder, int _Position) {
        final ListEntryItemWrapper singleEntryItemWrapper = mListOfEntries.get(_Position);
        final ListEntry            singleEntry            = singleEntryItemWrapper.getListEntry();

        switch (_ProductViewHolder.getItemViewType()) {
            default:
            case ViewType.NORMAL_VIEW:
                ViewHolder.ShoppingListProductViewHolder viewHolder = (ViewHolder.ShoppingListProductViewHolder) _ProductViewHolder;
                viewHolder.init(singleEntry);
                break;

            case ViewType.EDIT_MODE_VIEW:
                ViewHolder.ShoppingListEditProductViewHolder viewHolderEdit = (ViewHolder.ShoppingListEditProductViewHolder) _ProductViewHolder;
                viewHolderEdit.init(singleEntry);
                break;
            case ViewType.SELECTED_MODE_VIEW:
                // TODO: do some stuff :D
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mListOfEntries.size();
    }

    @Override
    public void addListEntry(String _ListEntryId) {
        ListEntry listEntry = ControllerFactory.getListController(mContext).getEntryById(_ListEntryId);
        mListOfEntries.add(new ListEntryItemWrapper(listEntry));
        Collections.sort(mListOfEntries, mCurrentComparator);
        // if here is a position like -1 there must be some strange effects going on.
        notifyItemInserted(getPositionForId(_ListEntryId));
    }

    @Override
    public void removeListEntry(String _ListEntryId) {
        int       positionToRemove = getPositionForId(_ListEntryId);
        if (positionToRemove < 0 || positionToRemove > mListOfEntries.size()) {
            Log.e(LOG_TAG, "Remove ListEntry from position " + positionToRemove + " is out of bounds");
            return;
        }
        mListOfEntries.remove(positionToRemove);
        notifyItemRemoved(positionToRemove);
    }

    @Override
    public void updateListEntry(String _ListEntryId) {
        ListEntry listEntry        = ControllerFactory.getListController(mContext).getEntryById(_ListEntryId);
        int       positionToUpdate = getPositionForId(_ListEntryId);
        if (positionToUpdate < 0 || positionToUpdate > mListOfEntries.size()) {
            Log.e(LOG_TAG, "Update ListEntry from position " + positionToUpdate + " is out of bounds");
            return;
        }
        mListOfEntries.set(positionToUpdate, new ListEntryItemWrapper(listEntry));

        Collections.sort(mListOfEntries, mCurrentComparator);
        int newPositionOfElement = getPositionForId(_ListEntryId);

        notifyItemChanged(positionToUpdate);
        notifyItemMoved(positionToUpdate, newPositionOfElement);
    }

    public ListEntry getItem(int _Position){
        return mListOfEntries.get(_Position).getListEntry();
    }

    @Override
    public long getItemId(int _Position) {
        return UUID.fromString(mListOfEntries.get(_Position).getListEntry().mUUID).
                getLeastSignificantBits();
    }

    @Override
    public void resetEditModeView() {
        int positionToUpdate = getPositionForId(mCurrentItemInEditMode.getListEntry().mUUID);
        if(positionToUpdate < 0){
            return;
        }

        mCurrentItemInEditMode.resetModeToNormalView();
        notifyItemChanged(positionToUpdate);
    }

    @Override
    public void setToEditMode(int _Position) {
        if (_Position < 0) {
            return;
        }
        mCurrentItemInEditMode = mListOfEntries.get(_Position);
        mCurrentItemInEditMode.setEditMode(true);
        notifyItemChanged(_Position);
    }

    @Override
    public void sortByComparator(Comparator<ListEntryItemWrapper> _Comparator) {
        mCurrentComparator = _Comparator;
        Collections.sort(mListOfEntries, _Comparator);
        notifyDataSetChanged();
    }

    @Override
    public int getPositionForId(String _Id) {
        int position = - 1;
        for (ListEntryItemWrapper listItem : mListOfEntries) {
            if (listItem.getListEntry().mUUID.equals(_Id)) {
                position = mListOfEntries.indexOf(listItem);
                break;
            }
        }
        return position;
    }
}
