package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Interface for accessing data of ShoppingList adapter.
 * Created by TS on 26.06.2015.
 */
public interface IShoppingListAdapter {
    /**
     * Adds the given ShoppingList to the adapter.
     * @param _ShoppingList the ShoppingList to be added.
     */
    void addList(ShoppingList _ShoppingList);

    /**
     * Updates the given ShoppingList in Adapter.
     * @param _ShoppingList the ShoppingList to be updated.
     */
    void updateList(ShoppingList _ShoppingList);

    /**
     * Removes the given ShoppingList from the adapter.
     * @param _ShoppingList The ShoppingList to remove.
     */
    void removeList(ShoppingList _ShoppingList);
}
