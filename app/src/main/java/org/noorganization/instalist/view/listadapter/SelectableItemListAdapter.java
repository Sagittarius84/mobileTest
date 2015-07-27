package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.view.activity.RecipeChangeActivity;
import org.noorganization.instalist.view.fragment.ProductChangeFragment;
import org.noorganization.instalist.view.fragment.ProductListDialogFragment;
import org.noorganization.instalist.view.interfaces.ISelectableItemListDataAdapter;
import org.noorganization.instalist.view.modelwrappers.IBaseListEntry;
import org.noorganization.instalist.view.sorting.selectableList.AlphabeticalIBaseListEntryComparator;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The adapter for displaying selectable items in the list.
 * Created by tinos_000 on 22.07.2015.
 */
public class SelectableItemListAdapter extends ArrayAdapter<IBaseListEntry> implements Filterable, ISelectableItemListDataAdapter {

    public static String LOG_TAG = SelectableItemListAdapter.class.toString();

    //region Private Attributes
    /**
     * The context of the app.
     */
    private Context mContext;

    /**
     * The parent Activity.
     */
    private Activity mActivity;

    /**
     * The Ressource id of the layout of a single item.
     */
    private int mLayoutId;

    /**
     * The Listentries for the list.
     */
    private List<IBaseListEntry> mFilteredListEntries;

    private List<IBaseListEntry> mAllListEntries;

    // Some thoughts about instance of usage
    //  It will affect it really negative when sorting happens, then all elements will be checked and this will result in a great performance loss.
    // usage of instance of is no bottleneck for this case, it will be called at max 30 times when first displayed or change was notified.
    // it depends on the screen size. But as stated in some measurements on0 this entry http://stackoverflow.com/a/26514984/2980948
    // it is save to work with with such a little dataset.
    // private ArrayList<Object> mFilteredListEntries;

    /**
     * The local comparator to be used for ordering the list.
     */
    private Comparator<IBaseListEntry> mComparator;

    /**
     * Indicates if the filter is active.
     */
    private boolean mFilterThreadActive;

    private Filter mFilter;

    /**
     * Indicates the current constraint, by which the list is filtered.
     */
    private IBaseListEntry.eItemType mCurrentFilterItemType;

    /**
     * The current Filter Constraint. Can contain the search string.
     */
    private String mCurrentFilterConstraint;

    /**
     * Used to determine if the data was added while thread is filtering.
     */
    private List<IBaseListEntry> mAddedItems;
    private Map<Integer, IBaseListEntry> mRemovedItems;
    private Map<Integer, IBaseListEntry> mChangedItems;

    //endregion

    //region Constructor

    /**
     * Default Constructor. Sets the comparator to sort by name.
     *
     * @param _Activity  the activity.
     * @param _Resource  the resource id for the custom listview.
     * @param _ListItems the items which should be displayed. They must implement IBaseListEntry.
     */
    public SelectableItemListAdapter(Activity _Activity, int _Resource, List<IBaseListEntry> _ListItems) {
        super(_Activity, _Resource, _ListItems);
        initialize(_Activity, _Resource, _ListItems);
    }

    /**
     * Constructor to modify comparator.
     *
     * @param _Activity  the context of the activity.
     * @param _Resource  the resource id for the custom listview.
     * @param _ListItems the items which should be displayed. They must implement IBaseListEntry.
     */
    public SelectableItemListAdapter(Activity _Activity, int _Resource, List<IBaseListEntry> _ListItems, Comparator<IBaseListEntry> _Comparator) {
        super(_Activity, _Resource, _ListItems);
        initialize(_Activity, _Resource, _ListItems);
        mComparator = _Comparator;
    }

    /**
     * Does the common initialization.
     */
    private void initialize(Activity _Activity, int _Resource, List<IBaseListEntry> _ListItems) {
        mActivity = _Activity;
        mContext = _Activity;
        mAllListEntries = _ListItems;
        mFilteredListEntries = _ListItems;
        mLayoutId = _Resource;
        mComparator = new AlphabeticalIBaseListEntryComparator();

        mCurrentFilterConstraint = ProductListDialogFragment.FILTER_SHOW_ALL;
        mCurrentFilterItemType = IBaseListEntry.eItemType.ALL;

        mAddedItems = new ArrayList<>();
        mRemovedItems = new HashMap<>();
        mChangedItems = new HashMap<>();
    }

    //endregion

    //region Viewholder
    private static class ViewHolder {
        CheckBox mcbItemChecked;
        TextView mtvItemName;
    }
    //endregion

    //region Adapter Methods

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view;
        IBaseListEntry thisEntry = mFilteredListEntries.get(_Position);

        if (_ConvertView == null) {
            ViewHolder viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(mLayoutId, null);
            viewHolder.mtvItemName = (TextView) view.findViewById(R.id.product_list_product_name);
            viewHolder.mcbItemChecked = (CheckBox) view.findViewById(R.id.product_list_product_selected);
            view.setLongClickable(true);
            view.setTag(viewHolder);
        } else {
            view = _ConvertView;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.mtvItemName.setText(thisEntry.getName());
        viewHolder.mcbItemChecked.setChecked(thisEntry.isChecked());

        view.setOnClickListener(new OnListEntryClickListener(thisEntry));
        view.setOnLongClickListener(new OnListEntryLongClickListener(thisEntry));

        return view;
    }

    @Override
    public int getCount() {
        return mFilteredListEntries.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemListFilter();
        }

        return mFilter;
    }

    //endregion

    //region Public Access
    @Override
    public void addItem(IBaseListEntry _ListEntry) {

        mAllListEntries.add(_ListEntry);
        if (mFilterThreadActive) {
            mAddedItems.add(_ListEntry);
        }

        if (isDataValidForFilteredList(_ListEntry)) {
            mFilteredListEntries.add(_ListEntry);
        }

        Collections.sort(mFilteredListEntries, mComparator);
        notifyDataSetChanged();
    }

    @Override
    public void changeItem(IBaseListEntry _ListEntry) {
        int index = mAllListEntries.indexOf(_ListEntry);
        if (index < 0) {
            Log.e(LOG_TAG, "changeItem: no such entry in this listentry");
            throw new IndexOutOfBoundsException("There is no elment like" + _ListEntry.toString() + " in the " + mAllListEntries.toString());
        }
        mAllListEntries.set(index, _ListEntry);

        if (isDataValidForFilteredList(_ListEntry)) {
            int filteredListItemIndex = mFilteredListEntries.indexOf(_ListEntry);
            if (filteredListItemIndex < 0) {
                Log.e(LOG_TAG, "changeItem: no such entry in filtered list.");
                throw new IndexOutOfBoundsException("There is no elment like" + _ListEntry.toString() + " in the " + mFilteredListEntries.toString());
            }
            mFilteredListEntries.set(filteredListItemIndex, _ListEntry);
        }

        if (mFilterThreadActive) {
            mChangedItems.put(_ListEntry.hashCode(), _ListEntry);
        }

        Collections.sort(mFilteredListEntries, mComparator);
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(IBaseListEntry _ListEntry) {
        // TODO: what happens if a Recipe entry has same id like a Product entry
        if (!mAllListEntries.remove(_ListEntry)) {
            Log.e(LOG_TAG, "Tried to remove entry, but failed.");
            return;
        }

        if (mFilterThreadActive) {
            mRemovedItems.put(_ListEntry.hashCode(), _ListEntry);
        }

        if (isDataValidForFilteredList(_ListEntry)) {
            if (!mFilteredListEntries.remove(_ListEntry)) {
                // TODO: throw exception ? or ignore this
                Log.e(LOG_TAG, "Tried to remove entry, but failed.");
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public IBaseListEntry getItem(int _Position) {
        return mFilteredListEntries.get(_Position);
    }

    @Override
    public Iterator<IBaseListEntry> getCheckedListEntries() {
        return mFilteredListEntries.iterator();
    }

    @Override
    public void setComparator(Comparator<IBaseListEntry> _Comaparator) {
        mComparator = _Comaparator;
        Collections.sort(mFilteredListEntries, mComparator);
        notifyDataSetChanged();
    }

    //endregion

    //region Private Methods

    /**
     * Checks if a given ListEntry can be added, changed or removed from the current filtered list according to the current filtertype.
     * If NAME_SEARCH typ is give, then an check will be made with the mCurrentFilterConstraint that contains the search.
     * Use it in case of add or change actions.
     *
     * @param _ListEntry the ListEntry to check.
     * @return true if it is, false if not.
     */
    private boolean isDataValidForFilteredList(IBaseListEntry _ListEntry) {
        if (mCurrentFilterItemType == _ListEntry.getType() || mCurrentFilterItemType == IBaseListEntry.eItemType.ALL) {
            return true;
        } else if (mCurrentFilterItemType == IBaseListEntry.eItemType.NAME_SEARCH) {
            if (_ListEntry.getName().toLowerCase().startsWith(mCurrentFilterConstraint.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    //endregion

    //region private Click listener
    private class OnListEntryClickListener implements View.OnClickListener {

        private IBaseListEntry mListEntry;

        public OnListEntryClickListener(IBaseListEntry _ListEntry) {
            mListEntry = _ListEntry;
        }

        @Override
        public void onClick(View _View) {
            CheckBox checkBox = (CheckBox) _View.findViewById(R.id.product_list_product_selected);

            mListEntry.setChecked(!mListEntry.isChecked());
            checkBox.setChecked(mListEntry.isChecked());

            int index = mAllListEntries.indexOf(mListEntry);
            if (index < 0 || index > mAllListEntries.size()) {
                Log.v(LOG_TAG, "OnListEntryClickListener entry to find in allListEntries is out of bounds");
                return;
            }
            mAllListEntries.set(index, mListEntry);
        }
    }

    /**
     * Saves an listentry to retrieve the data where the longclick was made.
     */
    private class OnListEntryLongClickListener implements View.OnLongClickListener {

        private IBaseListEntry mListEntry;

        public OnListEntryLongClickListener(IBaseListEntry _ListEntry) {
            mListEntry = _ListEntry;
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu actionMenu = new PopupMenu(getContext(), v);
            actionMenu.inflate(R.menu.menu_productrecipe_action_popup);
            actionMenu.setOnMenuItemClickListener(new OnListEntryPopupMenuClickListener(mListEntry));
            actionMenu.show();
            return true;
        }
    }


    private class OnListEntryPopupMenuClickListener implements PopupMenu.OnMenuItemClickListener {

        private IBaseListEntry mListEntry;

        public OnListEntryPopupMenuClickListener(IBaseListEntry _listEntry) {
            super();
            mListEntry = _listEntry;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    switch (mListEntry.getType()) {
                        case PRODUCT_LIST_ENTRY:
                            ViewUtils.addFragment(mActivity, ProductChangeFragment.
                                    newChangeInstance(((Product) mListEntry.getItem()).getId()));
                            break;
                        case RECIPE_LIST_ENTRY:
                            Intent startEditor = new Intent(getContext(), RecipeChangeActivity.class);
                            startEditor.putExtra(RecipeChangeActivity.ARGS_RECIPE_ID, mListEntry.getId());
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

        //region Private Methods

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
                    dialogBuilder.setMessage(getContext()
                            .getString(R.string.remove_product_question, mListEntry.getName()));

                    dialogBuilder.setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    IProductController productController = ControllerFactory.getProductController();

                                    if (productController.removeProduct((Product) mListEntry.getItem(), false)) {
                                        Toast.makeText(mContext, R.string.removed_product, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(mContext, R.string.removed_recipe, Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;
            }

            dialogBuilder.show();
        }

        //endregion
        private class SecondaryProductActionClickListener implements DialogInterface.OnClickListener {

            @Override
            public void onClick(DialogInterface _dialogInterface, int _whichButton) {
                if (_whichButton == DialogInterface.BUTTON_POSITIVE) {
                    IProductController productController = ControllerFactory.getProductController();
                    productController.removeProduct((Product) mListEntry.getItem(), true);
                    Toast.makeText(getContext(), R.string.removed_product, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //endregion

    //region Filter
    private class ItemListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence _Constraint) {
            mFilterThreadActive = true;
            FilterResults result = new FilterResults();
            List<IBaseListEntry> listEntries = new ArrayList<>(mAllListEntries);

            if (_Constraint == null || _Constraint.length() == 0) {
                result.values = listEntries;
                result.count = listEntries.size();
                return result;
            }

            IBaseListEntry.eItemType filterType;
            List<IBaseListEntry> filteredList = new ArrayList<>();

            switch (_Constraint.toString()) {
                case ProductListDialogFragment.FILTER_BY_PRODUCT:
                    filterType = IBaseListEntry.eItemType.PRODUCT_LIST_ENTRY;
                    break;
                case ProductListDialogFragment.FILTER_BY_RECIPE:
                    filterType = IBaseListEntry.eItemType.RECIPE_LIST_ENTRY;
                    break;
                case ProductListDialogFragment.FILTER_SHOW_ALL:
                    filterType = IBaseListEntry.eItemType.ALL;
                    filteredList = listEntries;
                    break;
                default:
                    // in this case the contraint is an string with the search term
                    filterType = IBaseListEntry.eItemType.NAME_SEARCH;

                    String contraintToFind = _Constraint.toString().toLowerCase();
                    for (IBaseListEntry entry : listEntries) {
                        if (entry.getName().toLowerCase().startsWith(contraintToFind)) {
                            filteredList.add(entry);
                        }
                    }
                    break;
            }

            if (filterType != IBaseListEntry.eItemType.ALL) {
                for (IBaseListEntry entry : listEntries) {
                    if (entry.getType() == filterType)
                        filteredList.add(entry);
                }
            }

            result.values = filteredList;
            result.count = filteredList.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence _Constraint, FilterResults _Results) {
            ArrayList<IBaseListEntry> entries = (ArrayList<IBaseListEntry>) _Results.values;
            mFilterThreadActive = false;
            mCurrentFilterConstraint = _Constraint.toString();

            if (mChangedItems.size() > 0 || mAddedItems.size() > 0 || mRemovedItems.size() > 0) {
                entries = resolveDirtyState(entries);
            }

            mFilteredListEntries = entries;

            mAddedItems.clear();
            mChangedItems.clear();
            mRemovedItems.clear();

            notifyDataSetChanged();
        }

        //region Private Methods

        private ArrayList<IBaseListEntry> resolveDirtyState(ArrayList<IBaseListEntry> _Entries) {
            switch (mCurrentFilterConstraint) {
                case ProductListDialogFragment.FILTER_BY_PRODUCT:
                    mCurrentFilterItemType = IBaseListEntry.eItemType.PRODUCT_LIST_ENTRY;
                    break;
                case ProductListDialogFragment.FILTER_BY_RECIPE:
                    mCurrentFilterItemType = IBaseListEntry.eItemType.PRODUCT_LIST_ENTRY;
                    break;
                case ProductListDialogFragment.FILTER_SHOW_ALL:
                    mCurrentFilterItemType = IBaseListEntry.eItemType.ALL;
                    break;
                default:
                    mCurrentFilterItemType = IBaseListEntry.eItemType.NAME_SEARCH;
                    break;
            }

            return resolveDirtyByType(_Entries);
        }

        private ArrayList<IBaseListEntry> resolveDirtyByType(ArrayList<IBaseListEntry> _Entries) {
            for (IBaseListEntry listEntry : _Entries) {
                if (isDataValidForFilteredList(listEntry)) {
                    if (mRemovedItems.containsKey(listEntry.hashCode())) {
                        _Entries.remove(listEntry);
                    } else if (mChangedItems.containsKey(listEntry.hashCode())) {
                        int index = _Entries.indexOf(listEntry);
                        _Entries.set(index, mChangedItems.get(listEntry.hashCode()));
                    }
                }
            }

            for (IBaseListEntry listEntry : mAddedItems) {
                if (isDataValidForFilteredList(listEntry)) {
                    _Entries.add(listEntry);
                }
            }
            return _Entries;
        }

        //endregion

    }


    //endregion
}
