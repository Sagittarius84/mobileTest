package org.noorganization.instalist.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.listadapter.ShoppingListAdapter;
import org.noorganization.instalist.view.listadapter.ShoppingListOverviewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * MainShoppingListView handles the display of an selected shoppinglist, so that the corresponding
 * items of this list are shown to the user.
 *
 * Is dependant on the selected list.
 * @author TS
 */
public class MainShoppingListView extends ActionBarActivity {

    public final static String KEY_LISTNAME = "list_name";

    private Toolbar     mToolbar;
    private ListView    mLeftSideListView;
    private ActionBarDrawerToggle mNavBarToggle;
    private DrawerLayout mDrawerLayout;

    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shopping_list_view);

        mTitle = "MainActivity";
        // init and setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout       = (DrawerLayout) findViewById(R.id.main_drawer_layout_container);
        mLeftSideListView   = (ListView) findViewById(R.id.list_view_left_side_navigation);


        List<ShoppingList> shoppingLists    = Select.from(ShoppingList.class).list();
        List<String> shoppingListNames     = new ArrayList<>();

        for(ShoppingList shoppingList : shoppingLists){
            // fill navbar with some sample data
            shoppingListNames.add(shoppingList.mName);
        }

        mLeftSideListView.setAdapter(new ShoppingListOverviewAdapter(this, shoppingListNames));

        mDrawerLayout.setFitsSystemWindows(true);

        // navbar custom design of toolbar
        mNavBarToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.nav_drawer_open,
                R.string.nav_drawer_close
        ){

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
                if(mToolbar.getTitle() != null) {
                    mTitle = mToolbar.getTitle().toString();
                    mToolbar.setTitle("Choose List");
                }
                // check if options menu has changed
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mNavBarToggle);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ShoppingListOverviewFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
  /*      if(mNavBarToggle.onOptionsItemSelected(item)){
            return true;
        }
*/
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
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

    // --------------------------------------------------------------------------------
    // private
    // --------------------------------------------------------------------------------

    public void selectList(String listName){

        Bundle args = new Bundle();
        args.putString(KEY_LISTNAME, listName);

        Fragment fragment = new ShoppingListOverviewFragment();
        fragment.setArguments(args);

        // create transaction to new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();

        mDrawerLayout.closeDrawer(mLeftSideListView);
    }

    /**
     * Sets the text of the toolbar title, when activity is updated.
     * @param _Title, the title of the toolbar
     */
    public void setToolbarTitle(String _Title){
        mTitle = _Title;
    }

    /**
     * A ShoppingListOverviewFragment containing a list view.
     */
    public static class ShoppingListOverviewFragment extends Fragment {

        private String mCurrentListName;
        private ActionBar mActionBar;
        private Context mContext;

        public ShoppingListOverviewFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle bundle = this.getArguments();
            if(bundle == null){
                return;
            }
            mCurrentListName = bundle.getString(MainShoppingListView.KEY_LISTNAME);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // get in here the actionbar
            mActionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            // not needed to check for null, because we have a actionbar always assigned
            mActionBar.setTitle(mCurrentListName);
            // set the title in "main" activity so that the current list name is shown on the actionbar
            ((MainShoppingListView) getActivity()).setToolbarTitle(mCurrentListName);
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();

            ListView shoppingListView;
            ShoppingListAdapter shoppingListAdapter;
            ShoppingList shoppingList;

            shoppingListView    = (ListView) getActivity().findViewById(R.id.fragment_shopping_list);

            if(mCurrentListName == null){
                shoppingList = Select.from(ShoppingList.class).first();
            }else {
                shoppingList = Select.from(ShoppingList.class).where(Condition.prop(
                        ShoppingList.LIST_NAME_ATTR).eq(mCurrentListName)).first();
            }
            if(shoppingList == null){
                return;
            }

            shoppingListAdapter = new ShoppingListAdapter(getActivity(), shoppingList.getEntries());
            shoppingListView.setAdapter(shoppingListAdapter);
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            return inflater.inflate(R.layout.fragment_main_shopping_list_view, container, false);

        }

    }
}
