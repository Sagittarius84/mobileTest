package org.noorganization.instalist.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ListController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.touchlistener.OnRecyclerItemTouchListener;
import org.noorganization.instalist.view.decoration.DividerItemListDecoration;
import org.noorganization.instalist.view.fragment.ProductCreationFragment;
import org.noorganization.instalist.view.listadapter.ShoppingListAdapter;
import org.noorganization.instalist.view.listadapter.ShoppingListOverviewAdapter;

import java.util.List;

/**
 * MainShoppingListView handles the display of an selected shoppinglist, so that the corresponding
 * items of this list are shown to the user.
 * <p/>
 * Is dependant on the selected list.
 *
 * @author TS
 */
public class MainShoppingListView extends ActionBarActivity {

    private final static String LOG_TAG = MainShoppingListView.class.getName();
    public final static String KEY_LISTNAME = "list_name";

    private Toolbar mToolbar;
    private ListView mLeftSideListView;

    /**
     * For creation an icon at the toolbar for toggling the navbar in and out.
     */
    private ActionBarDrawerToggle mNavBarToggle;

    /**
     * Layout reference of the side drawer navbar.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Title of the toolbar.
     */
    private String mTitle;

    /**
     * Name of the current list
     */
    private String mCurrentListName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shopping_list_view);

        // init and setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout_container);
        mLeftSideListView = (ListView) findViewById(R.id.list_view_left_side_navigation);

        // fill the list with selectable lists
        mLeftSideListView.setAdapter(new ShoppingListOverviewAdapter(this, GlobalApplication.getInstance().getShoppingListNames()));
        mDrawerLayout.setFitsSystemWindows(true);

        // navbar custom design of toolbar
        mNavBarToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.nav_drawer_open,
                R.string.nav_drawer_close
        ) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mToolbar.setTitle(mTitle);
                // check if options menu has changed
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (mToolbar.getTitle() != null) {
                    mTitle = mToolbar.getTitle().toString();
                    mToolbar.setTitle(R.string.choose_list);
                }
                // check if options menu has changed
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mNavBarToggle);

        if (savedInstanceState == null) {
            selectList(GlobalApplication.getInstance().getShoppingListNames().get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        mToolbar.inflateMenu(R.menu.menu_toolbar_main_listview);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // The action bar home/up action should open or close the navbar.
        // ActionBarDrawerToggle will take care of this.
        if (mNavBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // swtich which action item was pressed
        switch(id){
            case R.id.list_items_sort_by_amount:
                // say controller there is a statechange
                getFragmentManager().beginTransaction().replace(R.id.container, new ProductCreationFragment()).addToBackStack(null).commit();
                break;
            case R.id.list_items_sort_by_name:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavBarToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mNavBarToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 1){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }

    }

    // --------------------------------------------------------------------------------
    // own public methods
    // --------------------------------------------------------------------------------

    /**
     *
     * Creates a new fragment with the listentries of the given listname.
     * @param listName, name of the list that content should be shown.
     */
    public void selectList(String listName) {

        // always close the drawer
        mDrawerLayout.closeDrawer(mLeftSideListView);

        // list is the same as the current one
        // no need to do then something
        if(listName == mCurrentListName){
            return;
        }

        // decl
        Bundle args;
        Fragment fragment;

        // init
        mCurrentListName = listName;

        args = new Bundle();
        args.putString(KEY_LISTNAME, listName);

        fragment = new ShoppingListOverviewFragment();
        fragment.setArguments(args);

        changeFragment(fragment);
    }

    /**
     * Changes from the current fragment to the given fragment.
     * Adds the current fragment to the backstack.
     * @param fragment the fragment that should be created.
     */
    public void changeFragment(Fragment fragment){
        // create transaction to new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    /**
     * Sets the text of the toolbar title, when activity is updated.
     *
     * @param _Title, the title of the toolbar
     */
    public void setToolbarTitle(String _Title) {
        mTitle = _Title;
    }

    /**
     * A ShoppingListOverviewFragment containing a list view.
     */
    public static class ShoppingListOverviewFragment extends Fragment {

        private String  mCurrentListName;
        private ShoppingList    mCurrentShoppingList;

        private ActionBar mActionBar;
        private Context mContext;

        private ActionButton mAddButton;

        private LinearLayoutManager mLayoutManager;

        public ShoppingListOverviewFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // get bundle args to get the listname that should be shown
            Bundle bundle = this.getArguments();
            if (bundle == null) {
                return;
            }
            mCurrentListName    = bundle.getString(MainShoppingListView.KEY_LISTNAME);
            mContext            = this.getActivity();

        }

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

        @Override
        public void onPause() {
            super.onPause();
        }

        ShoppingListAdapter mShoppingListAdapter;


        @Override
        public void onResume() {
            super.onResume();

            // decl
            final RecyclerView shoppingListView;
            // init
            shoppingListView = (RecyclerView) getActivity().findViewById(R.id.fragment_shopping_list);
            // assign other listname if none is assigned
            if (mCurrentListName == null) {

                List<ShoppingList> mShoppingLists = ShoppingList.listAll(ShoppingList.class);
                if (mShoppingLists.size() > 0) {
                    mCurrentShoppingList   = mShoppingLists.get(0);
                    mCurrentListName = mCurrentShoppingList.mName;
                } else {
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
            shoppingListView.addOnItemTouchListener(new OnRecyclerItemTouchListener(mContext, shoppingListView){

                @Override
                public void onSwipeRight(View childView, int position) {
                    super.onSwipeRight(childView, position);
                    //int entryPosition = (int) shoppingListAdapter.getItemId(position);
                    ListEntry entry = GlobalApplication.getInstance().getListEntries(mCurrentListName).get(position);
                    TextView test = ((TextView) childView.findViewById(R.id.list_product_shopping_product_name));
                    test.setPaintFlags(
                            test.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);

                    ListController.getInstance().strikeItem(mCurrentShoppingList, entry.mProduct);

                    ListController.getInstance().removeItem(mCurrentShoppingList, entry.mProduct);
                    // just for showcasing
                    // mShoppingListAdapter.removeItem(position);
                }

                @Override
                public void onSwipeLeft(View childView, int position) {
                    super.onSwipeLeft(childView, position);
                    //int entryPosition = (int) shoppingListAdapter.getItemId(position);
                    ListEntry entry = GlobalApplication.getInstance().getListEntries(mCurrentListName).get(position);
                    GlobalApplication.getInstance().getListController().unstrikeItem(mCurrentShoppingList, entry.mProduct);
                }

                @Override
                public void onSingleTap(View childView, int position) {
                    super.onSingleTap(childView, position);
                    //int entryPosition = (int) shoppingListAdapter.getItemId(position);
                    ListEntry entry = GlobalApplication.getInstance().getListEntries(mCurrentListName).get(position);

                    Toast.makeText(getActivity(), "Item selected: " + entry.mProduct.mName, Toast.LENGTH_LONG);
                }

            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main_shopping_list_view, container, false);
            mAddButton = (ActionButton) view.findViewById(R.id.add_item_main_list_view);
            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Fragment fragment = new ProductCreationFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.commit();
                }
            });
            return view;

        }

    }
}
