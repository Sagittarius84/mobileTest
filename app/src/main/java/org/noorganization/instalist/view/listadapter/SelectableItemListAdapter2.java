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
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The adapter for displaying selectable items in the list.
 * Created by tinos_000 on 22.07.2015.
 */
public class SelectableItemListAdapter2 extends ArrayAdapter<IBaseListEntry> implements Filterable, ISelectableItemListDataAdapter {

    public static String LOG_TAG = SelectableItemListAdapter2.class.toString();

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
    public SelectableItemListAdapter2(Activity _Activity, int _Resource, List<IBaseListEntry> _ListItems) {
        super(_Activity, _Resource, _ListItems);
        initialize(_Activity, _Resource, _ListItems);

        // TODO: set comparator mComparator;
    }

    /**
     * Constructor to modify comparator.
     *
     * @param _Activity  the context of the activity.
     * @param _Resource  the resource id for the custom listview.
     * @param _ListItems the items which should be displayed. They must implement IBaseListEntry.
     */
    public SelectableItemListAdapter2(Activity _Activity, int _Resource, List<IBaseListEntry> _ListItems, Comparator<IBaseListEntry> _Comparator) {
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
        View view = null;
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
        mFilteredListEntries.add(_ListEntry);
        mAllListEntries.add(_ListEntry);
        if (mFilterThreadActive) {
            mAddedItems.add(_ListEntry);
        }
        // Collections.sort(mFilteredListEntries, mComparator);
    }

    @Override
    public void changeItem(IBaseListEntry _ListEntry) {
        int index = mFilteredListEntries.indexOf(_ListEntry);
        int index2 = mAllListEntries.indexOf(_ListEntry);
        if (index < 0 || index2 < 0) {
            Log.v(LOG_TAG, "changeItem: no such entry in this listentry");
            return;
        }
        mAllListEntries.set(index2, _ListEntry);
        mFilteredListEntries.set(index, _ListEntry);
        if (mFilterThreadActive) {
            mAddedItems.add(_ListEntry);
        }
    }

    @Override
    public void removeItem(IBaseListEntry _ListEntry) {
        mAllListEntries.remove(_ListEntry);
        if (mFilteredListEntries.remove(_ListEntry)) {
            if (mFilterThreadActive) {
                mRemovedItems.put(_ListEntry.hashCode(), _ListEntry);
            }
        }
    }

    @Override
    public IBaseListEntry getItem(int _Position) {
        return mFilteredListEntries.get(_Position);
    }

    @Override
    public Iterator<IBaseListEntry> getCheckedListEntries() {
        return mFilteredListEntries.iterator();
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
        }
    }

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
                    filterType = IBaseListEntry.eItemType.EMPTY;
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

            if (filterType != IBaseListEntry.eItemType.EMPTY) {
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
            if (mChangedItems.size() > 0 || mAddedItems.size() > 0 || mRemovedItems.size() > 0) {
                entries = resolveDirtyState(_Constraint, entries);
            }

            mFilteredListEntries = entries;
            mFilterThreadActive = false;
            notifyDataSetChanged();
        }

        //region Private Methods

        private ArrayList<IBaseListEntry> resolveDirtyState(CharSequence _Constraint, ArrayList<IBaseListEntry> _Entries) {
            switch (_Constraint.toString()) {
                case ProductListDialogFragment.FILTER_BY_PRODUCT:
                    _Entries = resolveDirtyByType(_Entries, IBaseListEntry.eItemType.PRODUCT_LIST_ENTRY, null);
                    break;
                case ProductListDialogFragment.FILTER_BY_RECIPE:
                    _Entries = resolveDirtyByType(_Entries, IBaseListEntry.eItemType.RECIPE_LIST_ENTRY, null);
                    break;
                case ProductListDialogFragment.FILTER_SHOW_ALL:
                    // set to all elements
                    break;
                default:
                    _Entries = resolveDirtyByType(_Entries, IBaseListEntry.eItemType.NAME_SEARCH, _Constraint.toString().toLowerCase());
                    break;
            }

            mAddedItems.clear();
            mChangedItems.clear();
            mRemovedItems.clear();
            return _Entries;
        }

        private ArrayList<IBaseListEntry> resolveDirtyByType(ArrayList<IBaseListEntry> _Entries, IBaseListEntry.eItemType _FilterType, String _SearchString) {
            for (IBaseListEntry listEntry : _Entries) {
                if (listEntry.getType() != _FilterType) {
                    continue;
                }
                if (mRemovedItems.containsKey(listEntry.hashCode())) {
                    _Entries.remove(listEntry);
                }
                if (mChangedItems.containsKey(listEntry.hashCode())) {
                    int index = _Entries.indexOf(listEntry);
                    if (_FilterType == IBaseListEntry.eItemType.NAME_SEARCH) {
                        if (listEntry.getName().toLowerCase().startsWith(_SearchString)) {
                            _Entries.set(index, mChangedItems.get(listEntry.hashCode()));
                        }
                    } else {
                        _Entries.set(index, mChangedItems.get(listEntry.hashCode()));
                    }
                }
            }

            if (mAddedItems.size() > 0) {
                for (IBaseListEntry listEntry : mAddedItems) {
                    if (listEntry.getType() == _FilterType) {
                        if (_FilterType == IBaseListEntry.eItemType.NAME_SEARCH) {
                            if (listEntry.getName().toLowerCase().startsWith(_SearchString)) {
                                _Entries.add(listEntry);
                            }
                        } else {
                            _Entries.add(listEntry);
                        }
                    }
                }
            }
            return _Entries;
        }

        //endregion

    }


    //endregion
}
