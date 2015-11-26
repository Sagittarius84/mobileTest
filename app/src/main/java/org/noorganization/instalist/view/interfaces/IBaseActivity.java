package org.noorganization.instalist.view.interfaces;

import android.app.Fragment;
import android.view.View;

/**
 * Provides standard functions to manipulate activity specific fields from a fragment.
 */
// TODO: Remove ISideDrawerListDataEvents when events are propagated
public interface IBaseActivity {
    /**
     * Changes the current Fragment to the given Fragment.
     * @param _NewFragment The Fragment to move to.
     */
    void    changeFragment(Fragment _NewFragment);

    /**
     * Set the toolbar title to the given string.
     * @param _ToolbarTitle the string to display on the toolbar.
     */
    void    setToolbarTitle(String _ToolbarTitle);

    /**
     * Set the DrawerLayout mode. (Closed, Open,..)
     * @param _DrawerLayoutMode the DrawerLayout mode.
     */
    void setDrawerLockMode(int _DrawerLayoutMode);

    /**
     * Called when Back was pressed.
     */
    void onBackPressed();

    /**
     * Sets the Navigation Icon.
     * @param _ResId the resource id of the resource. (drawable|mipmap)
     */
    //void setNavigationIcon(int _ResId);

    /**
     * Set the NavigationClickListener.
     * @param _ClickListener the click listener that implements the logic when pressed.
     */
    void setNavigationClickListener(View.OnClickListener _ClickListener);

    /**
     * Updates the DrawerLayout. Needed to provide that it is working. Also updates the option menu.
     * Best place to call it, before a fragment is removed and the user should be led to the ShoppingList overview.
     */
    void bindDrawerLayout();

    /**
     * UnRegister the given view for context menu.
     * @param _View the view to be registered for contextmenu.
     */
    void unregisterForContextMenu(View _View);

    /**
     * Register the view for the contextmenu.
     * @param _View The view to be registered for the contextmenu.
     */
    void registerForContextMenu(View _View);

    /**
     * Create a new listener with the given category id.
     * @param _CategoryId the Id of category where the category should be placed in.
     */
    void setSideDrawerAddListButtonListener(String _CategoryId);

    void registerFragment(Fragment fragment);
    void unregisterFragment(Fragment fragment);
}