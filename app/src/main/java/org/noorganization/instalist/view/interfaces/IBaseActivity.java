package org.noorganization.instalist.view.interfaces;

import android.app.Fragment;
import android.view.View;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

/**
 * Provides standard functions to manipulate activity specific fields from a fragment.
 */
public interface IBaseActivity{
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
    void setNavigationIcon(int _ResId);

    /**
     * Set the NavigationClickListener.
     * @param _ClickListener the click listener that implements the logic when pressed.
     */
    void setNavigationClickListener(View.OnClickListener _ClickListener);

    /**
     * Updates the DrawerLayout. Needed to provide that is working.
     */
    void updateDrawerLayout();

    /**
     * Updates the view of shoppinglists.
     * @param _ShoppingList The _ShoppingList that was changed.
     */
    void updateChangedShoppingList(ShoppingList _ShoppingList);

    /**
     * Updates the view of categories.
     * @param _Category The _Category that was changed.
     */
    void updateChangedCategory(Category _Category);
}