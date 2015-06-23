package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.software.shell.fab.ActionButton;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.ChangeHandler;
import org.noorganization.instalist.view.MainShoppingListView;
import org.noorganization.instalist.view.datahandler.SelectableBaseItemListEntryDataHolder;
import org.noorganization.instalist.view.decoration.DividerItemListDecoration;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.listadapter.ShoppingListAdapter;
import org.noorganization.instalist.view.sorting.AlphabeticalListEntryComparator;
import org.noorganization.instalist.view.sorting.PriorityListEntryComparator;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A ShoppingListOverviewFragment containing a list view.
 */
public class ShoppingListOverviewFragment extends Fragment{

    private String mCurrentListName;
    private ShoppingList mCurrentShoppingList;

    private ActionBar mActionBar;
    private Context mContext;

    private ActionButton mAddButton;

    private LinearLayoutManager mLayoutManager;

    private ShoppingListAdapter mShoppingListAdapter;

    private IListController mListController;

    private IBaseActivity mBaseActivityInterface;

    private static String PREFERENCES_NAME = "SHOPPING_LIST_FRAGMENT";

    private static String SORT_MODE = "SORT_MODE";
    /**
     * Contains the mapping from a Integer to comperators.
     */
    private Map<Integer, Comparator> mMapComperable;

    private static Integer SORT_BY_NAME = 0;
    private static Integer SORT_BY_PRIORITY = 1;

    // --------------------------------------------------------------------------------------------


    public ShoppingListOverviewFragment() {
    }


    /**
     * Creates an instance of an ShoppingListOverviewFragment.
     * @param _ListName the name of the list that should be shown.
     * @return the new instance of this fragment.
     */
    public static ShoppingListOverviewFragment newInstance(String _ListName){

        ShoppingListOverviewFragment fragment = new ShoppingListOverviewFragment();
        Bundle args = new Bundle();
        args.putString(MainShoppingListView.KEY_LISTNAME, _ListName);
        fragment.setArguments(args);
        return fragment;
    }




    // --------------------------------------------------------------------------------------------


    @Override
    public void onAttach(Activity _Activity) {
        super.onAttach(_Activity);
        mContext            = _Activity;

        try {
            mBaseActivityInterface = (IBaseActivity) _Activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Activity.toString()
                    + " has no IBaseActivity interface attached.");
        }

        mListController     = ControllerFactory.getListController();
        ((ChangeHandler)((GlobalApplication)getActivity().getApplication()).getChangeHandler()).setCurrentFragment(this);
        mBaseActivityInterface.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // get bundle args to get the listname that should be shown
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }
        mCurrentListName     = bundle.getString(MainShoppingListView.KEY_LISTNAME);
        mCurrentShoppingList = ShoppingList.findByName(mCurrentListName);
        mMapComperable = new WeakHashMap<>();
        mMapComperable.put(0, new AlphabeticalListEntryComparator());
        mMapComperable.put(1, new PriorityListEntryComparator());
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sortDetails = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        // swtich which action item was pressed
        switch (id) {
            case R.id.list_items_sort_by_priority:
                mShoppingListAdapter.sortByComparator(mMapComperable.get(SORT_BY_PRIORITY));

                sortDetails.edit()
                        .putInt(SORT_MODE, SORT_BY_PRIORITY)
                        .apply();

                break;
            case R.id.list_items_sort_by_name:
                mShoppingListAdapter.sortByComparator(mMapComperable.get(SORT_BY_NAME));
                sortDetails.edit()
                        .putInt(SORT_MODE, SORT_BY_NAME)
                        .apply();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set the title in "main" activity so that the current list name is shown on the actionbar
        mBaseActivityInterface.setToolbarTitle(mCurrentListName);
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onPause() {
        super.onPause();
        mAddButton.setOnClickListener(null);
        ((ChangeHandler) ((GlobalApplication) getActivity().getApplication()).getChangeHandler()).setCurrentFragment(null);
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sortDetails = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        // decl
        final RecyclerView shoppingListView;
        // init
        shoppingListView = (RecyclerView) getActivity().findViewById(R.id.fragment_shopping_list);

        // assign other listname if none is assigned
        if (mCurrentListName == null) {

            List<ShoppingList> mShoppingLists = ShoppingList.listAll(ShoppingList.class);
            if (mShoppingLists.size() > 0) {
                mCurrentShoppingList = mShoppingLists.get(0);
                mCurrentListName = mCurrentShoppingList.mName;
                mBaseActivityInterface.setToolbarTitle(mCurrentShoppingList.mName);
            } else {
                mBaseActivityInterface.setToolbarTitle(mContext.getResources().getString(R.string.shopping_list_overview_fragment_no_list_available));
                // do something to show that there are no shoppinglists!
                return;
            }
        }

        mShoppingListAdapter = new ShoppingListAdapter(getActivity(), GlobalApplication.getInstance().getListEntries(mCurrentListName));
        mShoppingListAdapter.sortByComparator(mMapComperable.get(sortDetails.getInt(SORT_MODE, SORT_BY_PRIORITY)));

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        shoppingListView.setLayoutManager(mLayoutManager);
        shoppingListView.addItemDecoration(new DividerItemListDecoration(getResources().getDrawable(R.drawable.list_divider), false, false));
        shoppingListView.setAdapter(mShoppingListAdapter);
        shoppingListView.setItemAnimator(new DefaultItemAnimator());

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset selected items ... (lazy resetting!)
                SelectableBaseItemListEntryDataHolder.getInstance().clear();
                ViewUtils.addFragment(getActivity(),
                        ProductListDialogFragment.newInstance(mCurrentShoppingList.getId()));
            }
        });

        mBaseActivityInterface.updateDrawerLayout();
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        super.onCreateView(_Inflater, _Container, _SavedInstanceState);

        View view = _Inflater.inflate(R.layout.fragment_main_shopping_list_view, _Container, false);
        mAddButton = (ActionButton) view.findViewById(R.id.add_item_main_list_view);

        return view;
    }

    /**
     * Updates the adapter in the shoppinglistadapter with the given item.
     * @param _Entry the item that should be deleted.
     */
    public void onListItemUpdated(ListEntry _Entry){
        mShoppingListAdapter.changeItem(_Entry);
    }

    /**
     * Removes the given item from the containing listarray in the shoppinglistadapter.
     * @param _Entry the item that should be deleted.
     */
    public void onListItemDeleted(ListEntry _Entry){
        mShoppingListAdapter.removeItem(_Entry);
    }

    public void onShoppingListItemChanged(ShoppingList _ShoppingList){
        mBaseActivityInterface.updateChangedShoppingList(_ShoppingList);
    }
}