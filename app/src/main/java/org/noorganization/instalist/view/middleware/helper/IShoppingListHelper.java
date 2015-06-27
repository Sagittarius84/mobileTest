package org.noorganization.instalist.view.middleware.helper;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.interfaces.ICategoryAdapter;
import org.noorganization.instalist.view.interfaces.IShoppingListAdapter;

/**
 * General interface to communicate with the single helper classes.
 * Created by tinos_000 on 25.06.2015.
 */
public interface IShoppingListHelper extends IShoppingListAdapter, ICategoryAdapter{

    /**
     * Called to create a context menu relating to the current list.
     *
     * @param _Menu     the context menu where the menu items should be added.
     * @param _View     the given View by onCreateContextMenu.
     * @param _MenuInfo the given MenuInfo given by onCreteContextMenu.
     * @return the extended ContextMenu.
     */
    ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo);

    void onContextMenuItemClicked(MenuItem _Item);

    /**
     * Checks if the current ListRenderer is active.
     *
     * @return true if active, false if inactive.
     */
    boolean isActive();

    /**
     * Set the List that should be rendered to true, the other lists to false. Also sets the visibility to visible or gone.
     *
     * @param _IsActive true if the ShoppingList should be rendered else it is not.
     */
    void setActiveState(boolean _IsActive);

    /**
     * Used to update the underlying adapterdata to the current state. Used to prevent the recreation of the helper strucutre.
     */
    void updateAdapter();

}
