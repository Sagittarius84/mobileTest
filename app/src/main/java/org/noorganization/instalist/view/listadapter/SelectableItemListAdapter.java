package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarRecord;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.view.activity.RecipeChangeActivity;
import org.noorganization.instalist.view.dataholder.SelectableBaseItemListEntryDataHolder;
import org.noorganization.instalist.view.fragment.ProductChangeFragment;
import org.noorganization.instalist.view.fragment.ProductListDialogFragment;
import org.noorganization.instalist.view.modelwrappers.IBaseListEntry;
import org.noorganization.instalist.view.modelwrappers.SelectableBaseItemListEntry;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the adapter for handling Selectable data such as products and recipes.
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
        View view;
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

        private final IBaseListEntry mListEntry;

        public OnLongClickListenerListEntry(IBaseListEntry _ListEntry) {
            mListEntry = _ListEntry;
        }

      @Override
        public boolean onLongClick(View v) {
            PopupMenu actionMenu = new PopupMenu(getContext(), v);
            actionMenu.inflate(R.menu.menu_productrecipe_action_popup);
            //actionMenu.setOnMenuItemClickListener(new OnListEntryPopupMenuClickListener(mListEntry));
            actionMenu.show();
            return true;
        }
    }
/*
    private class OnListEntryPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

        private IBaseListEntry mListEntry;

        public OnListEntryPopupMenuClickListener(IBaseListEntry _listEntry) {
            super();
            mListEntry = _listEntry;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            SugarRecord item = ((SugarRecord) mListEntry.getEntry().getObject());
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    switch (mListEntry.getType()) {
                        case PRODUCT_LIST_ENTRY:
                            ViewUtils.addFragment(mActivity, ProductChangeFragment.
                                    newChangeInstance(item.getId()));
                            break;
                        case RECIPE_LIST_ENTRY:
                            Intent startEditor = new Intent(getContext(), RecipeChangeActivity.class);
                            startEditor.putExtra(RecipeChangeActivity.ARGS_RECIPE_ID, item.getId());
                            getContext().startActivity(startEditor);
                            break;
                        default:
                            throw new IllegalStateException("There is no entry type defined.");
                    }
                    break;
                case R.id.action_delete:
                    onDelete();
                    break;
                default:
                    return false;
            }
            return true;
        }

        private void onDelete() {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            switch (mListEntry.getType()) {
                case PRODUCT_LIST_ENTRY:
                    dialogBuilder.setMessage(getContext().
                            getString(R.string.remove_product_question, mListEntry.getName()));
                    dialogBuilder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            IProductController productController = ControllerFactory.getProductController();
                            if (productController.removeProduct(
                                    (Product) mListEntry.getEntry().getObject(), false)) {
                                Toast.makeText(getContext(), R.string.removed_product, Toast.LENGTH_SHORT).
                                        show();
                            } else {
                                AlertDialog.Builder secondaryBuilder = new AlertDialog.Builder(getContext());
                                DialogInterface.OnClickListener clickListener =
                                        new SecondaryProductActionClickListener();
                                secondaryBuilder.setPositiveButton(android.R.string.yes, clickListener);
                                secondaryBuilder.setNegativeButton(android.R.string.no, clickListener);
                                secondaryBuilder.setMessage(R.string.remove_product_question2);
                                secondaryBuilder.show();
                            }
                        }
                    });
                    break;
                case RECIPE_LIST_ENTRY:
                    dialogBuilder.setMessage(getContext().
                            getString(R.string.remove_recipe_question, mListEntry.getName()));
                    dialogBuilder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            IRecipeController recipeController = ControllerFactory.getRecipeController();
                            recipeController.removeRecipe((Recipe) mListEntry.getItem());
                            Toast.makeText(getContext(), R.string.removed_recipe, Toast.LENGTH_SHORT).
                                    show();
                        }
                    });
                    break;
            }

            dialogBuilder.show();
        }

        private class SecondaryProductActionClickListener implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface _dialogInterface, int _whichButton) {
                if (_whichButton == DialogInterface.BUTTON_POSITIVE) {
                    IProductController productController = ControllerFactory.getProductController();
                    productController.removeProduct((Product) mListEntry.getEntry().getObject(), true);
                    Toast.makeText(getContext(), R.string.removed_product, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
*/
    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                // TODO: make it thread safe
                List<SelectableBaseItemListEntry> listEntries = new ArrayList<>(mResSelectableItems);

                if (constraint == null || constraint.length() == 0) {
                    result.values = listEntries;
                    result.count = listEntries.size();
                } else {
                    IBaseListEntry.eItemType filterType;
                    ArrayList<SelectableBaseItemListEntry> filteredList = new ArrayList<>();

                    switch (constraint.toString()) {
                        case ProductListDialogFragment.FILTER_BY_PRODUCT:
                            filterType = IBaseListEntry.eItemType.PRODUCT_LIST_ENTRY;
                            break;
                        case ProductListDialogFragment.FILTER_BY_RECIPE:
                            filterType = IBaseListEntry.eItemType.RECIPE_LIST_ENTRY;
                            break;
                        case ProductListDialogFragment.FILTER_SHOW_ALL:
                            filterType = IBaseListEntry.eItemType.EMPTY;
                            break;
                        default:
                            filterType = IBaseListEntry.eItemType.NAME_SEARCH;

                            String contraintToFind = constraint.toString().toLowerCase();
                            for (SelectableBaseItemListEntry entry : listEntries) {
                                if (entry.getItemListEntry().getName().toLowerCase().startsWith(contraintToFind)) {
                                    filteredList.add(entry);
                                }
                            }
                            break;
                    }

                    if (filterType != IBaseListEntry.eItemType.EMPTY) {
                        for (SelectableBaseItemListEntry entry : listEntries) {
                            if (entry.getItemListEntry().getType() == filterType)
                                filteredList.add(entry);
                        }
                    } else {
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
