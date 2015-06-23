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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.touchlistener.IOnShoppingListClickListenerEvents;
import org.noorganization.instalist.touchlistener.sidebar.OnCancelClickListenerWithData;
import org.noorganization.instalist.touchlistener.sidebar.OnCancelMoveClickListener;
import org.noorganization.instalist.touchlistener.sidebar.OnSubmitClickListenerWithChildData;
import org.noorganization.instalist.touchlistener.sidebar.OnSubmitClickListenerWithParentData;
import org.noorganization.instalist.touchlistener.sidebar.OnSubmitMoveClickListener;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.interfaces.IBaseActivityListEvent;
import org.noorganization.instalist.view.listadapter.CategoryListAdapter;
import org.noorganization.instalist.view.listadapter.ExpandableCategoryItemListAdapter;
import org.noorganization.instalist.view.listadapter.PlainShoppingListOverviewAdapter;
import org.noorganization.instalist.view.utils.PreferencesManager;
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
public class MainShoppingListView extends ActionBarActivity implements IBaseActivity, IBaseActivityListEvent, IOnShoppingListClickListenerEvents {

    private final static String LOG_TAG      = MainShoppingListView.class.getName();
    private final static String DEFAULT_CATEGORY_NAME = "(default)";

    public final static  String KEY_LISTNAME = "list_name";

    private static final int GROUP_MENU                         = 2;
    private static final int GROUP_MENU_ADD_LIST_ACTION         = 0;
    private static final int GROUP_MENU_REMOVE_CATEGORY_ACTION  = 1;
    private static final int GROUP_MENU_EDIT_CATEGORY_ACTION    = 2;
    private static final int CHILD_MENU                         = 3;
    private static final int CHILD_MENU_EDIT_LIST_NAME_ACTION   = 0;
    private static final int CHILD_MENU_REMOVE_LIST_ACTION      = 1;
    private static final int CHILD_MENU_MOVE_TO_CATEGORY_ACTION = 2;

    private Toolbar mToolbar;

    private ExpandableListView mExpandableListView;
    private ListView           mPlainListView;

    private RelativeLayout mLeftMenuDrawerRelativeLayout;
    private EditText       mNewNameEditText;

    private Button mAddListButton;
    private Button mAddCategoryButton;

    private ExpandableCategoryItemListAdapter mCategoryItemListAdapter;
    private PlainShoppingListOverviewAdapter  mPlainShoppingListAdapter;

    /**
     * For creation of an icon at the toolbar to toggle the navbar.
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

    private long mDefaultCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shopping_list_view);

        // init PreferencesManager
        PreferencesManager.initializeInstance(this);
        mDefaultCategoryId = PreferencesManager.getInstance().getLongValue(PreferencesManager.KEY_DEFAULT_CATEGORY_ID);
        if(mDefaultCategoryId == -1L){
            // if not set
            mDefaultCategoryId = ControllerFactory.getCategoryController().createCategory(DEFAULT_CATEGORY_NAME).getId();
        }

        // init and setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout_container);
        mExpandableListView = (ExpandableListView) findViewById(R.id.drawer_layout_custom_list_name_view);
        mPlainListView = (ListView) findViewById(R.id.drawer_layout_custom_plain_shopping_list_overview);
        mNewNameEditText = (EditText) findViewById(R.id.drawer_layout_custom_new_name);

        mAddListButton = (Button) findViewById(R.id.drawer_layout_custom_create_list);
        mAddCategoryButton = (Button) findViewById(R.id.drawer_layout_custom_create_category);

        mLeftMenuDrawerRelativeLayout = (RelativeLayout) findViewById(R.id.list_view_left_side_navigation);
        mSettingsButton = (Button) findViewById(R.id.drawer_layout_custom_settings);

        long categoryCount = Category.count(Category.class, null, new String[]{});
        // check if there are categories
        if (categoryCount > 1) {
            // more then 1 category, then display shoppinglists in expandable view
            mCategoryItemListAdapter = new ExpandableCategoryItemListAdapter(this, Category.listAll(Category.class));
            // fill the list with selectable lists
            mExpandableListView.setAdapter(mCategoryItemListAdapter);
        } else {
            // display the shoppinglists in plain style
            mPlainShoppingListAdapter = new PlainShoppingListOverviewAdapter(this, ShoppingList.listAll(ShoppingList.class));
            mPlainListView.setAdapter(mPlainShoppingListAdapter);
        }

        registerForContextMenu(mExpandableListView);

        mDrawerLayout.setFitsSystemWindows(true);
        assignDrawer();


        if (savedInstanceState == null) {
            if (ShoppingList.count(ShoppingList.class, null, new String[]{}) > 0) {
                selectList(ShoppingList.listAll(ShoppingList.class).get(0));
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.i(LOG_TAG, "Context menu called.");
        ExpandableListView.ExpandableListContextMenuInfo contextMenuInfo =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int entityType    = ExpandableListView.getPackedPositionType(contextMenuInfo.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(contextMenuInfo.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(contextMenuInfo.packedPosition);

        switch (entityType) {
            case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                menu.setHeaderTitle(getString(R.string.category_action));
                menu.add(GROUP_MENU, GROUP_MENU_ADD_LIST_ACTION, 1, getString(R.string.add_list));
                menu.add(GROUP_MENU, GROUP_MENU_REMOVE_CATEGORY_ACTION, 2, getString(R.string.remove_category));
                menu.add(GROUP_MENU, GROUP_MENU_EDIT_CATEGORY_ACTION, 3, getString(R.string.edit_category_name));
                break;
            case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                menu.setHeaderTitle(getString(R.string.shopping_list_action));
                menu.add(CHILD_MENU, CHILD_MENU_EDIT_LIST_NAME_ACTION, 1, getString(R.string.edit_shopping_list));
                menu.add(CHILD_MENU, CHILD_MENU_REMOVE_LIST_ACTION, 2, getString(R.string.remove_shopping_list));
                menu.add(CHILD_MENU, CHILD_MENU_MOVE_TO_CATEGORY_ACTION, 3, getString(R.string.move_category));
                break;
            default:
                return;
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "Context menu item selected.");
        onContextItemClickProcessor(item);
        return super.onContextItemSelected(item);
    }


    /**
     * Handles the clicked ContextOption and processes the corresponding processes.
     * @param item The MenuItem that was clicked.
     */
    private void onContextItemClickProcessor(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo contextMenuInfo =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int entityType    = ExpandableListView.getPackedPositionType(contextMenuInfo.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(contextMenuInfo.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(contextMenuInfo.packedPosition);

        int groupId = item.getGroupId();
        int itemId  = item.getItemId();

        View view;

        int flatPosition         = mExpandableListView.getFlatListPosition(contextMenuInfo.packedPosition);
        int firstVisiblePosition = mExpandableListView.getFirstVisiblePosition();

        view = mExpandableListView.getChildAt(flatPosition - firstVisiblePosition);

        switch (groupId) {
            case GROUP_MENU:
                //region GROUP_MENU
                Category category = (Category) mCategoryItemListAdapter.getGroup(groupPosition);

                switch (itemId) {
                    case GROUP_MENU_ADD_LIST_ACTION:
                        Toast.makeText(this, "Add list on group: " + ((Category) mCategoryItemListAdapter.getGroup(groupPosition)).mName, Toast.LENGTH_SHORT).show();
                        // best practice would be change dynamically id? or insert an edit text field at the first pos....
                        break;
                    case GROUP_MENU_REMOVE_CATEGORY_ACTION:
                        Toast.makeText(this, "Remove group: " + ((Category) mCategoryItemListAdapter.getGroup(groupPosition)).mName, Toast.LENGTH_SHORT).show();
                        ControllerFactory.getCategoryController().removeCategory(category);
                        onCategoryRemoved(category);
                        break;
                    case GROUP_MENU_EDIT_CATEGORY_ACTION:
                        //region Edit Category name
                        Toast.makeText(this, "Rename group: " + ((Category) mCategoryItemListAdapter.getGroup(groupPosition)).mName, Toast.LENGTH_SHORT).show();

                        final EditText editText;
                        ImageView cancelView, submitView;
                        ViewSwitcher viewSwitcher;

                        cancelView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_cancel);
                        submitView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_submit);

                        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.expandable_list_view_view_switcher);
                        editText = (EditText) view.findViewById(R.id.expandable_list_view_category_name_edit);

                        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
                        submitView.setOnClickListener(new OnSubmitClickListenerWithParentData(viewSwitcher, editText, category.getId(), mCategoryItemListAdapter));

                        editText.setText(category.mName);
                        // consume the longClickEvents and also mark whole edittext text
                        /* Probably use this otherwise.
                        editText.setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View v) {
                                EditText newName = (EditText) v.findViewById(R.id.expandable_list_view_category_name_edit);
                                newName.selectAll();
                                return true;
                            }
                        });
                         */
                        viewSwitcher.showNext();
                        //endregion
                        break;
                }
                //endregion GROUP_MENU
                break;
            case CHILD_MENU:
                //region CHILD_MENU
                ShoppingList shoppingList = (ShoppingList) mCategoryItemListAdapter.getChild(groupPosition, childPosition);
                Category categoryForShoppingList = (Category) mCategoryItemListAdapter.getGroup(groupPosition);

                EditText editText;
                ViewSwitcher viewSwitcher;
                ImageView cancelView, submitView;

                viewSwitcher = (ViewSwitcher) view.findViewById(R.id.expandable_list_view_view_switcher);
                // get the shoppinglist from database
                shoppingList = ShoppingList.findById(ShoppingList.class, shoppingList.getId());
                switch (itemId) {
                    case CHILD_MENU_EDIT_LIST_NAME_ACTION:

                        editListName(view, shoppingList, viewSwitcher);
                        break;
                    case CHILD_MENU_REMOVE_LIST_ACTION:
                        removeList(shoppingList);
                        break;
                    case CHILD_MENU_MOVE_TO_CATEGORY_ACTION:
                        changeCategoryOfList(view, shoppingList, categoryForShoppingList, viewSwitcher);
                        break;
                }
                //endregion CHILD_MENU
                break;
        }
    }

    private void changeCategoryOfList(View view, ShoppingList shoppingList, Category categoryForShoppingList, ViewSwitcher viewSwitcher) {
        ImageView cancelView;
        ImageView submitView;LinearLayout moveContainer;
        Spinner                           spinner;

        moveContainer = (LinearLayout) view.findViewById(R.id.expandable_list_view_choose_move_category);

        cancelView = (ImageView) view.findViewById(R.id.expandable_list_view_move_cancel);
        submitView = (ImageView) view.findViewById(R.id.expandable_list_view_move_submit);
        spinner = (Spinner) view.findViewById(R.id.expandable_list_view_list_move_spinner);

        List<Category> categories = Category.listAll(Category.class);
        categories.remove(categoryForShoppingList);

        SpinnerAdapter spinnerAdapter = new CategoryListAdapter(this, categories);
        spinner.setAdapter(spinnerAdapter);

        viewSwitcher.setVisibility(View.GONE);
        moveContainer.setVisibility(View.VISIBLE);

        cancelView.setOnClickListener(new OnCancelMoveClickListener(moveContainer, viewSwitcher));
        submitView.setOnClickListener(new OnSubmitMoveClickListener(moveContainer, viewSwitcher, spinner, shoppingList));
    }

    private void removeList(ShoppingList shoppingList) {
        boolean deleted = ControllerFactory.getListController().removeList(shoppingList);
        if (! deleted) {
            Toast.makeText(this, getString(R.string.deletion_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void editListName(View view, ShoppingList shoppingList, ViewSwitcher viewSwitcher) {
        ImageView cancelView;
        ImageView submitView;
        EditText  editText;
        cancelView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_submit);

        editText = (EditText) view.findViewById(R.id.expandable_list_view_list_edit_name);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithChildData(viewSwitcher, editText, shoppingList.getId()));

        editText.setText(shoppingList.mName);
        viewSwitcher.showNext();
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
        if (drawerOpen) {
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
                if (listName == null) {
                    return;
                }


                ShoppingList shoppingList = ControllerFactory.getListController().addList(listName);
                //ControllerFactory.getListController().moveToCategory(shoppingList, Category.find);
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
    }


    /**
     * Evaluates the EditText field for the new name {listname or category name} and returns the inserted text.
     *
     * @return the text that was inserted, else null.
     */

    private String validateAndGetNewName() {
        mNewNameEditText.setError(null);
        if (! ViewUtils.checkEditTextIsFilled(mNewNameEditText)) {
            mNewNameEditText.setError(getString(R.string.drawer_layout_custom_no_input));
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
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void exitApp() {
        super.onBackPressed();
    }

    // --------------------------------------------------------------------------------
    // own public methods
    // --------------------------------------------------------------------------------


    @Override
    public void selectList(ShoppingList _ShoppingList) {
        if(_ShoppingList.mName == null){
            throw new NullPointerException("Name of ShoppingList must be set.");
        }


        // always close the drawer
        mDrawerLayout.closeDrawer(mLeftMenuDrawerRelativeLayout);

        // list is the same as the current one
        // no need to do then something
        if (_ShoppingList.mName.equals(mCurrentListName)) {
            return;
        }

        // decl
        Bundle   args;
        Fragment fragment;

        // init
        mCurrentListName = _ShoppingList.mName;
        fragment = ShoppingListOverviewFragment.newInstance(_ShoppingList.mName);
        ViewUtils.addFragment(this, fragment);
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
        Log.w("NoOrg Deprecated", "MainShoppingListView's changeFragment.");
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
    public void bindDrawerLayout() {
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
        mCategoryItemListAdapter.updateCategory(_Category);
    }


    /**
     * Pass created category to the underlying adapter.
     * And also checks if it needs to switch layouts from plain to expandablelistview.
     * @param _Category The created Category.
     */
    public void onCategoryCreated(Category _Category){
        long categoryCount = 0L;

        if(categoryCount == 1L || mCategoryItemListAdapter == null){
           // if it is equal to 1 or the adapter is zero(not assigned) then we need to switch from plain list to ExpandableList with categories
           // and also push the shoppinglist without categories into a separate category
           assignExpandableList();
        }else{
            mCategoryItemListAdapter.addCategory(_Category);
        }

    }

    /**
     * Set the PlainListView as current listview to display the shoppinglists.
     */
    private void assignPlainList(){
        mPlainShoppingListAdapter = new PlainShoppingListOverviewAdapter(this, ShoppingList.listAll(ShoppingList.class));
        mCategoryItemListAdapter = null;

        mExpandableListView.setAdapter(mPlainShoppingListAdapter);
        mPlainListView.setAdapter(mPlainShoppingListAdapter);

        mExpandableListView.setVisibility(View.GONE);
        mPlainListView.setVisibility(View.VISIBLE);
    }

    /**
     * Set the ExpandableListView to display categories and shoppinglists.
     */
    private void assignExpandableList() {

            mPlainShoppingListAdapter = null;
            mCategoryItemListAdapter = new ExpandableCategoryItemListAdapter(this, Category.listAll(Category.class));

            mPlainListView.setAdapter(mPlainShoppingListAdapter);
            mExpandableListView.setAdapter(mCategoryItemListAdapter);

            mPlainListView.setVisibility(View.GONE);
            mExpandableListView.setVisibility(View.VISIBLE);

    }

    /**
     * Pass the category which has been removed.
     * @param _RemovedCategory the category that has been removed.
     */
    public void onCategoryRemoved(Category _RemovedCategory) {
        long categoryCount = 0L;
        if(categoryCount == 1L){
            assignPlainList();
        } else{
            mCategoryItemListAdapter.removeCategory(_RemovedCategory);
        }

    }

    @Override
    public void onShoppingListClicked(ShoppingList _ShoppingList) {
        selectList(_ShoppingList);
    }

    /**
     * Set a drawer to a fragmentlayout.
     */
    public void assignDrawer() {
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
                String tmpTitle = getString(R.string.choose_list);
                mToolbar.setTitle(tmpTitle);
                // check if options menu has changed
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mNavBarToggle);
    }
}
