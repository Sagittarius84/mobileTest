package org.noorganization.instalist.view;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
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

    private ListView    mLeftSideListView;
    private EditText    mNewListEditText;
    private Button      mAddListButton;
    private ShoppingListOverviewAdapter mShoppingListOverviewAdapter;
    private RelativeLayout mLeftMenuDrawerRelativeLayout;
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

        List<String> shoppingListNames = ShoppingList.getShoppingListNames();
        // init and setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout_container);
        mLeftSideListView = (ListView) findViewById(R.id.drawer_layout_custom_list_name_view);
        mNewListEditText = (EditText) findViewById(R.id.drawer_layout_custom_new_listname_edittext);
        mAddListButton = (Button) findViewById(R.id.drawer_layout_custom_add_list_name_button);
        mLeftMenuDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.list_view_left_side_navigation);

        mShoppingListOverviewAdapter = new ShoppingListOverviewAdapter(this, shoppingListNames);
        // fill the list with selectable lists
        mLeftSideListView.setAdapter(mShoppingListOverviewAdapter);
        mDrawerLayout.setFitsSystemWindows(true);

        assignDrawer();

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
        switch (id) {
            case R.id.list_items_sort_by_amount:
                // say controller there is a statechange
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
    protected void onResume() {
        super.onResume();
        mAddListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewListEditText.setError(null);

                if (mNewListEditText.getText().length() < 0) {
                    mNewListEditText.setError(getResources().getString(R.string.drawer_layout_custom_no_input));
                    return;
                }
                String listName = mNewListEditText.getText().toString();

                ShoppingList shoppingList = ControllerFactory.getListController().addList(listName);
                if (shoppingList == null) {
                    mNewListEditText.setError(getResources().getString(R.string.drawer_layout_custom_exists));
                    return;
                }

                // clear the field
                mNewListEditText.setText("");
                mShoppingListOverviewAdapter.addList(shoppingList.mName);
                changeFragment(ShoppingListOverviewFragment.newInstance(shoppingList.mName));
            }
        });

        mNewListEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                ((EditText) v).setError(null);
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAddListButton.setOnClickListener(null);
        mNewListEditText.setOnKeyListener(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mNavBarToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            // create a leave message box to prevent accidentially exit the app.
            new AlertDialog.Builder(this)
                    .setTitle("Exit App?")
                    .setMessage("Are you sure you want to leave this georgeous app?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            exitApp();
                        }
                    })
                    .setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }

    // --------------------------------------------------------------------------------
    // own public methods
    // --------------------------------------------------------------------------------

    /**
     * Creates a new fragment with the listentries of the given listname.
     *
     * @param listName, name of the list that content should be shown.
     */
    public void selectList(String listName) {

        // always close the drawer
        mDrawerLayout.closeDrawer(mLeftMenuDrawerRelativeLayout);

        // list is the same as the current one
        // no need to do then something
        if (listName == mCurrentListName) {
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
     * Get the assigned toolbar reference.
     * @return the reference of the toolbar.
     */
    public Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * Get the assigned DrawerLayout, use it for locking it on fragments.
     * @return the reference to the DrawerLayout.
     */
    public DrawerLayout getDrawerLayout(){
        return mDrawerLayout;
    }

    /**
     * Changes from the current fragment to the given fragment.
     * Adds the current fragment to the backstack.
     *
     * @param _Fragment the fragment that should be created.
     */
    public void changeFragment(Fragment _Fragment) {
        // create transaction to new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, _Fragment);
        transaction.addToBackStack(null);
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
     * Add the selected products to list? or say that we should update the view.
     */
    public void addProductsToList() {

    }

    /**
     * Sets the drawer to toolbar.
     */
    public void assignDrawer(){
        mToolbar.setNavigationIcon(R.mipmap.ic_menu_black_36dp);
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
                mToolbar.setNavigationIcon(R.mipmap.ic_menu_black_36dp);
                // check if options menu has changed
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (mToolbar.getTitle() != null) {
                    mTitle = mToolbar.getTitle().toString();
                    mToolbar.setTitle(R.string.choose_list);
                    mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_18dp);
                }
                // check if options menu has changed
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mNavBarToggle);
    }

}
