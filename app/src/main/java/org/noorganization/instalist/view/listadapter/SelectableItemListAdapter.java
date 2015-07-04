package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.orm.SugarRecord;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.view.BaseItemListEntry;
import org.noorganization.instalist.model.view.SelectableBaseItemListEntry;
import org.noorganization.instalist.view.datahandler.SelectableBaseItemListEntryDataHolder;
import org.noorganization.instalist.view.fragment.ProductChangeFragment;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 25.05.2015.
 */
public class SelectableItemListAdapter extends ArrayAdapter<SelectableBaseItemListEntry> implements Filterable{

    final String LOG_TAG    = SelectableProductListAdapter.class.getName();

    private Activity mActivity;
    private List<SelectableBaseItemListEntry> mSelectableItems;
    private List<SelectableBaseItemListEntry> mResSelectableItems;


    public SelectableItemListAdapter(Activity _activity, List<SelectableBaseItemListEntry> _ProductList){
        super(_activity, R.layout.list_selectable_product  , _ProductList);
        mActivity = _activity;
        mSelectableItems = _ProductList;
        mResSelectableItems = new ArrayList<>(_ProductList);

        if(SelectableBaseItemListEntryDataHolder.getInstance().getListEntries().size() > 0) {
            mSelectableItems = SelectableBaseItemListEntryDataHolder.getInstance().getListEntries();
        }else{
            SelectableBaseItemListEntryDataHolder.getInstance().setListEntries(mSelectableItems);
        }
        SelectableBaseItemListEntryDataHolder.getInstance().setListEntries(mSelectableItems);

    }

    @Override
    public int getCount() {
        return mSelectableItems.size();
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;
        SelectableBaseItemListEntry listEntry      = mSelectableItems.get(_Position);

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mActivity.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(R.layout.list_selectable_product, null);
        }else{
            view = _ConvertView;
        }

        TextView textView       = (TextView) view.findViewById(R.id.product_list_product_name);
        CheckBox checkBox       = (CheckBox) view.findViewById(R.id.product_list_product_selected);

        textView.setText(listEntry.getItemListEntry().getName());
        checkBox.setChecked(listEntry.isChecked());

        view.setOnClickListener(new OnClickListenerListEntry(listEntry));
        view.setOnLongClickListener(new OnLongClickListenerListEntry(listEntry.getItemListEntry()));
        return view;
    }

    /**
     * Fetches all occurences of selected entries.
     * @return a list of all selected entries.
     */
    public List<SelectableBaseItemListEntry> getSelectedEntries(){
        List<SelectableBaseItemListEntry> selectedEntries = new ArrayList<>();
        for(SelectableBaseItemListEntry entry : mSelectableItems){
            if(entry.isChecked()) {
                mSelectableItems.add(entry);
            }
        }
        return selectedEntries;
    }

    /**
     * OnClickListenerListEntry handles the onClick on a view that contains an SelectableBaseItemListEntry and changes the underlying data in this adapter.
     */
    private class OnClickListenerListEntry implements View.OnClickListener {

        private SelectableBaseItemListEntry mItemListEntry;
        private CheckBox    mCheckBox;

        public OnClickListenerListEntry(SelectableBaseItemListEntry listEntry){
            mItemListEntry = listEntry;
        }

        @Override
        public void onClick(View v) {
            if(mCheckBox == null) {
                mCheckBox = (CheckBox) v.findViewById(R.id.product_list_product_selected);
            }
            mItemListEntry.setChecked(!mItemListEntry.isChecked());
            mCheckBox.setChecked(mItemListEntry.isChecked());

            // save the change of data
            SelectableBaseItemListEntryDataHolder.getInstance().setListEntries(mSelectableItems);
        }
    }

    /**
     * Saves an listentry to retrieve the data where the longclick was made.
     */
    private class OnLongClickListenerListEntry implements View.OnLongClickListener {

        private final BaseItemListEntry   mListEntry;

        public OnLongClickListenerListEntry(BaseItemListEntry _ListEntry){
            mListEntry = _ListEntry;
        }

        @Override
        public boolean onLongClick(View v) {
            Fragment nextFragment;
            SugarRecord currentEntry = (SugarRecord) (mListEntry.getEntry().getObject());

            switch (mListEntry.getType()){
                case PRODUCT_LIST_ENTRY:
                    nextFragment = ProductChangeFragment.newChangeInstance(currentEntry.getId());
                    break;
                case RECIPE_LIST_ENTRY:
                    // TODO: start recipe editor
                    //nextFragment = RecipeChangeActivity.newInstance(
                    //        mCurrentShoppingList.mName, currentEntry.getId());
                    //break;
                    return true;
                default:
                    throw new IllegalStateException("There is no entry type defined.");
            }

            ViewUtils.addFragment(mActivity, nextFragment);

            return true;
        }
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();

                // TODO: make it thread safe
                List<SelectableBaseItemListEntry> listEntries = new ArrayList<>( mResSelectableItems);

                if(constraint == null || constraint.length() == 0){
                    result.values = listEntries;
                    result.count = listEntries.size();
                }else{
                    BaseItemListEntry.eItemType filterType;
                    ArrayList<SelectableBaseItemListEntry> filteredList = new ArrayList<SelectableBaseItemListEntry>();

                    switch(Integer.parseInt(constraint.toString())){
                        case 0:
                            filterType = BaseItemListEntry.eItemType.PRODUCT_LIST_ENTRY;
                            break;
                        case 1:
                            filterType = BaseItemListEntry.eItemType.RECIPE_LIST_ENTRY;
                            break;
                        case 2:
                            filterType = BaseItemListEntry.eItemType.EMPTY;
                            break;
                        default:
                            filterType = BaseItemListEntry.eItemType.EMPTY;
                            break;
                    }

                    if(filterType != BaseItemListEntry.eItemType.EMPTY) {
                        for (SelectableBaseItemListEntry entry : listEntries) {
                            if (entry.getItemListEntry().getType() == filterType)
                                filteredList.add(entry);
                        }
                    }
                    else {
                        filteredList = new ArrayList<>(mResSelectableItems);
                    }
                    result.values = filteredList;
                    result.count = filteredList.size();
                }

                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mSelectableItems = (ArrayList<SelectableBaseItemListEntry>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

}
