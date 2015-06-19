package org.noorganization.instalist.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.TintEditText;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.interfaces.IBaseActivityListEvent;
import org.noorganization.instalist.view.listadapter.ExpandableCategoryItemListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.List;

/**
 * MainShoppingListView handles the display of an selected shoppinglist, so that the corresponding
 * items of this list are shown to the user.
 * <p/>
 * Is dependant on the selected list.
 *
 * @author TS
 */
public class MainShoppingListView extends ActionBarActivity implements IBaseActivity, IBaseActivityListEvent {

    private final static String LOG_TAG = MainShoppingListView.class.getName();
    public final static String KEY_LISTNAME = "list_name";

    private Toolbar mToolbar;

    private ExpandableListView mExpandableListView;
    private EditText    mNewNameEditText;

    private Button      mAddListButton;
    private Button      mAddCategoryButton;

    private ExpandableCategoryItemListAdapter mCategoryItemListAdapter;
    private RelativeLayout mLeftMenuDrawerRelativeLayout;

    /**
     * For creation an icon at the toolbar for toggling the navbar in and out.
     */
    private ActionBarDrawerToggle mNavBarToggle;

    /**
     * Layout reference of the side drawer navbar.
     */
    private DrawerLayout mDrawerLayout;

    private Button mSettingsButton;

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

        mDrawerLayout       = (DrawerLayout) findViewById(R.id.main_drawer_layout_container);
        mExpandableListView = (ExpandableListView) findViewById(R.id.drawer_layout_custom_list_name_view);
        mNewNameEditText    = (EditText) findViewById(R.id.drawer_layout_custom_new_name);

        mAddListButton      = (Button) findViewById(R.id.drawer_layout_custom_create_list);
        mAddCategoryButton  = (Button) findViewById(R.id.drawer_layout_custom_create_category);

        mLeftMenuDrawerRelativeLayout   = (RelativeLayout) findViewById(R.id.list_view_left_side_navigation);
        mCategoryItemListAdapter        = new ExpandableCategoryItemListAdapter(this, Category.listAll(Category.class));
        mSettingsButton                 = (Button) findViewById(R.id.drawer_layout_custom_settings);

        // fill the list with selectable lists
        mExpandableListView.setAdapter(mCategoryItemListAdapter);
        mDrawerLayout.setFitsSystemWindows(true);

        assignDrawer();

        if (savedInstanceState == null) {
            selectList(ShoppingList.listAll(ShoppingList.class).get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //mToolbar.inflateMenu(R.menu.menu_toolbar_main_listview);
        // check if the drawer is open

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mLeftMenuDrawerRelativeLayout);
        if(drawerOpen){
            menu.clear();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // The action bar home/up action should open or close the navbar.
        // ActionBarDrawerToggle will take care of this.
        // Consume the the onOptionsItemSelected event.
        if (mNavBarToggle.onOptionsItemSelected(item)) {
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
    protected void onResume() {
        super.onResume();
        // TODO: try to remove this
        mCategoryItemListAdapter.notifyDataSetChanged();
        // end todo

        mAddListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String listName = validateAndGetNewName();
                if(listName == null){
                    return;
                }

                ShoppingList shoppingList = ControllerFactory.getListController().addList(listName);
                if (shoppingList == null) {
                    mNewNameEditText.setError(getResources().getString(R.string.list_exists));
                    return;
                }

                // clear the field
                mNewNameEditText.setText("");
                mCategoryItemListAdapter.notifyDataSetChanged();

                changeFragment(ShoppingListOverviewFragment.newInstance(shoppingList.mName));
            }
        });

        mAddCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = validateAndGetNewName();
                if (categoryName == null) {
                    return;
                }

                // create new category if insert of category failed, there will be shown an
                // error hint to the user.
                Category category = ControllerFactory.getCategoryController().createCategory(categoryName);
                if (category == null) {
                    mNewNameEditText.setError(getResources().getString(R.string.category_exists));
                    return;
                }
                mCategoryItemListAdapter.addCategory(category);
            }
        });

        mNewNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((EditText) v).setError(null);
                }
            }
        });
        mNewNameEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                ((EditText) v).setError(null);
                return false;
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent;
            }
        });

        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View _View, int position, long id) {
                long packedPosition = mExpandableListView.getExpandableListPosition(position);
                if(_View == null) return false;

                ImageView cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
                ImageView submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

                switch(ExpandableListView.getPackedPositionType(packedPosition)){
                    case ExpandableListView.PACKED_POSITION_TYPE_CHILD:

                        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                        ShoppingList shoppingList = (ShoppingList) mCategoryItemListAdapter.getChild(groupPosition, childPosition);

                        ViewSwitcher viewSwitcher = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
                        EditText editText = (EditText) _View.findViewById(R.id.expandable_list_view_list_edit_name);

                        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
                        submitView.setOnClickListener(new OnSubmitClickListenerWithChildData(viewSwitcher, editText, childPosition, groupPosition));

                        editText.setText(shoppingList.mName);
                        viewSwitcher.showNext();

                        return true;

                    case ExpandableListView.PACKED_POSITION_TYPE_GROUP:

                        groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);

                        Category category   = (Category) mCategoryItemListAdapter.getGroup(groupPosition);
                        viewSwitcher        = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
                        editText            = (EditText) _View.findViewById(R.id.expandable_list_view_category_name_edit);

                        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
                        submitView.setOnClickListener(new OnSubmitClickListenerWithParentData(viewSwitcher, editText, groupPosition));
                        editText.setText(category.mName);
                        viewSwitcher.showNext();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    private class OnCancelClickListenerWithData implements View.OnClickListener{

        private ViewSwitcher mViewSwitcher;

        public OnCancelClickListenerWithData(ViewSwitcher _ViewSwitcher){
            mViewSwitcher = _ViewSwitcher;
        }

        @Override
        public void onClick(View v) {
            mViewSwitcher.showNext();
        }
    }
    private class OnSubmitClickListenerWithParentData implements View.OnClickListener{

        private ViewSwitcher mViewSwitcher;
        private EditText mNameEditText;
        private int mParentIndex;

        public OnSubmitClickListenerWithParentData(ViewSwitcher _ViewSwitcher, EditText _NameEditText, int _ParentIndex){
            mViewSwitcher = _ViewSwitcher;
            mParentIndex = _ParentIndex;
            mNameEditText = _NameEditText;
        }

        @Override
        public void onClick(View v) {
            String insertedText = mNameEditText.getText().toString();

            Category category   = (Category) mCategoryItemListAdapter.getGroup(mParentIndex);
            ControllerFactory.getCategoryController().renameCategory(category, insertedText);
            mViewSwitcher.showNext();
            // TODO: remove this with a callback.
            updateChangedCategory(category);
        }
    }

    private class OnSubmitClickListenerWithChildData implements View.OnClickListener{

        private ViewSwitcher mViewSwitcher;
        private EditText mNameEditText;
        private int mChildIndex;
        private int mParentIndex;

        public OnSubmitClickListenerWithChildData(ViewSwitcher _ViewSwitcher, EditText _NameEditText, int _ChildIndex, int _ParentIndex){
            mViewSwitcher = _ViewSwitcher;
            mParentIndex = _ParentIndex;
            mChildIndex = _ChildIndex;
            mNameEditText = _NameEditText;
        }

        @Override
        public void onClick(View v) {
            String insertedText = mNameEditText.getText().toString();

            ShoppingList shoppingList   = (ShoppingList) mCategoryItemListAdapter.getChild(mParentIndex, mChildIndex);
            ControllerFactory.getListController().renameList(shoppingList, insertedText);
            mViewSwitcher.showNext();
        }
    }

    /**
     * Evaluates the EditText field for the new name {listname or category name} and returns the inserted text.
     * @return the text that was inserted, else null.
     */
    private String validateAndGetNewName(){
        mNewNameEditText.setError(null);

        if (mNewNameEditText.getText().length() < 0) {
            mNewNameEditText.setError(getResources().getString(R.string.drawer_layout_custom_no_input));
            return null;
        }
        return mNewNameEditText.getText().toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAddListButton.setOnClickListener(null);
        mNewNameEditText.setOnKeyListener(null);
        mSettingsButton.setOnClickListener(null);
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

    public void exitApp(){
        super.onBackPressed();
    }

    // --------------------------------------------------------------------------------
    // own public methods
    // --------------------------------------------------------------------------------


    @Override
    public void selectList(ShoppingList _ShoppingList) {

        // always close the drawer
        mDrawerLayout.closeDrawer(mLeftMenuDrawerRelativeLayout);

        // list is the same as the current one
        // no need to do then something
        if (_ShoppingList.mName == mCurrentListName) {
            return;
        }

        // decl
        Bundle args;
        Fragment fragment;

        // init
        mCurrentListName = _ShoppingList.mName;
        fragment = ShoppingListOverviewFragment.newInstance(_ShoppingList.mName);
        changeFragment(fragment);
    }

    @Override
    public void setDrawerLockMode(int _DrawerLayoutMode) {
        mDrawerLayout.setDrawerLockMode(_DrawerLayoutMode);
    }

    /**
     * Changes from the current fragment to the given fragment.
     * Adds the current fragment to the backstack.
     *
     * @param _Fragment the fragment that should be created.
     */
    @Override
    public void changeFragment(Fragment _Fragment) {
        ViewUtils.addFragment(this, _Fragment);
    }

    @Override
    public void setNavigationIcon(int _ResId) {
        mToolbar.setNavigationIcon(_ResId);
    }

    @Override
    public void setNavigationClickListener(View.OnClickListener _ClickListener) {
        mToolbar.setNavigationOnClickListener(_ClickListener);
    }

    @Override
    public void updateDrawerLayout() {
        assignDrawer();
    }

    /**
     * Sets the text of the toolbar title, when activity is updated.
     *
     * @param _Title, the title of the toolbar
     */
    @Override
    public void setToolbarTitle(String _Title) {
        mTitle = _Title;
    }

    @Override
    public void updateChangedShoppingList(ShoppingList _ShoppingList) {
        mCategoryItemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateChangedCategory(Category _Category) {
        mCategoryItemListAdapter.notifyDataSetChanged();
    }

    /**
     * Sets the drawer to toolbar.
     */
    public void assignDrawer(){
        mToolbar.setNavigationIcon(R.mipmap.ic_menu_white_36dp);
        // navbar custom design of toolbar
        mNavBarToggle = new ActionBarDrawerToggle(
                this,                       // host activity
                mDrawerLayout,              // DrawerLayout instance
                mToolbar,                   // Reference to toolbar
                R.string.nav_drawer_open,   // text for drawer on open icon
                R.string.nav_drawer_close   // text for drawer on close icon
        ) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mToolbar.setNavigationIcon(R.mipmap.ic_menu_white_36dp);
                mToolbar.setTitle(mTitle);
                // check if options menu has changed
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_36dp);
                if (mToolbar.getTitle() != null) {
                    mTitle = R.string.choose_list + mToolbar.getTitle().toString();
                    mToolbar.setTitle(mTitle);
                }
                // check if options menu has changed
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mNavBarToggle);
    }
}
