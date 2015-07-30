package org.noorganization.instalist.view.sidedrawermodelwrapper;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import org.noorganization.instalist.view.interfaces.ICategoryAdapter;
import org.noorganization.instalist.view.interfaces.IShoppingListAdapter;

/**
 * The base of the SideDrawerListManager.
 * Created by TS on 26.06.2015.
 */
public interface ISideDrawerListManager extends ICategoryAdapter, IShoppingListAdapter {

    /**
     * Call it from onContextMenuItemClicked of Activity/Fragment.
     * @param _Item The MenuItem to retrieve data.
     */
    void onContextMenuItemClicked(MenuItem _Item);

    /**
     * Extends the given ContextMenu with content.
     * @param _Menu The ContextMenu given by onCreateContextMenu of Activity/Fragment.
     * @param _View The general View given by onCreateContextMenu of Activity/Fragment.
     * @param _MenuInfo The MenuInfo given by onCreateContextMenu of Activity/Fragment.
     * @return
     */
    ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo);
}
