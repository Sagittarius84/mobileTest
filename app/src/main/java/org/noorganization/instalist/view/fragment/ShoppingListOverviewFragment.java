package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LauncherActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import org.noorganization.instalist.view.listadapter.ShoppingListAdapter;

import java.util.List;

/**
 * A ShoppingListOverviewFragment containing a list view.
 */
public class ShoppingListOverviewFragment extends BaseCustomFragment {

    private String mCurrentListName;
    private ShoppingList mCurrentShoppingList;

    private ActionBar mActionBar;
    private Context mContext;

    private ActionButton mAddButton;

    private LinearLayoutManager mLayoutManager;

    private ShoppingListAdapter mShoppingListAdapter;

    private IListController mListController;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get bundle args to get the listname that should be shown
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }
        mCurrentListName    = bundle.getString(MainShoppingListView.KEY_LISTNAME);
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get in here the actionbar
        mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        // not needed to check for null, because we have a actionbar always assigned
        mActionBar.setTitle(mCurrentListName);
        // set the title in "main" activity so that the current list name is shown on the actionbar
        ((MainShoppingListView) getActivity()).setToolbarTitle(mCurrentListName);
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onPause() {
        super.onPause();
        mAddButton.setOnClickListener(null);
        ((ChangeHandler)((GlobalApplication)getActivity().getApplication()).getChangeHandler()).setCurrentFragment(null);
    }


    // --------------------------------------------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();

        mContext            = this.getActivity();
        mListController     = ControllerFactory.getListController();
        ((ChangeHandler)((GlobalApplication)getActivity().getApplication()).getChangeHandler()).setCurrentFragment(this);
        unlockDrawerLayout();

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
                setToolbarTitle(mCurrentShoppingList.mName);
            } else {
                setToolbarTitle(mActivity.getResources().getString(R.string.shopping_list_overview_fragment_no_list_available));
                // do something to show that there are no shoppinglists!
                return;
            }
        }

        mShoppingListAdapter = new ShoppingListAdapter(getActivity(), GlobalApplication.getInstance().getListEntries(mCurrentListName));
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        shoppingListView.setLayoutManager(mLayoutManager);
        shoppingListView.addItemDecoration(new DividerItemListDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha), false, false));
        shoppingListView.setAdapter(mShoppingListAdapter);
        shoppingListView.setItemAnimator(new DefaultItemAnimator());

    }


    // --------------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main_shopping_list_view, container, false);
        mAddButton = (ActionButton) view.findViewById(R.id.add_item_main_list_view);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset selected items ... (lazy resetting!)
                SelectableBaseItemListEntryDataHolder.getInstance().clear();
                Fragment fragment = ProductListDialogFragment.newInstance(mCurrentListName);
                changeFragment(fragment);
            }
        });
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
}