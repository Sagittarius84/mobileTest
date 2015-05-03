package org.noorganization.instalist.controller;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;

/**
 * IListController is the (by software engineering created) interface for modifying
 * shopping lists. If there is no documentation for the methods, following is always valid:
 * - for parameters, null's are not allowed. If set to null, no modification will be made and false
 *   will be returned.
 * - it will be always returned whether the modification worked.
 * Created by michi on 20.04.2015.
 */
public interface IListController {
    /**
     * Adds or updates an item to an existing list.
     * @param _list A valid ShoppingList, not null.
     * @param _product A valid Product, not null.
     * @param _amount The amount of the product. Not +infty, NaN or lesser than 0.001f.
     * @return The created or updated ListEntry. If not worked, null or the old item will be
     * returned.
     */
    ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount);

    /**
     * Strikes a ListEntry.
     * @param _list A valid ShoppingList, not null.
     * @param _product A valid Product, not null.
     * @return The changed item or null, if not found or parameters invalid.
     */
    ListEntry strikeItem(ShoppingList _list, Product _product);

    /**
     * Removes the virtual stroke from a ListEntry.
     * @param _list A valid ShoppingList, not null.
     * @param _product A valid Product, not null.
     * @return The changed item or null, if not found or parameters invalid.
     */
    ListEntry unstrikeItem(ShoppingList _list, Product _product);

    /**
     * Alias for {@link #strikeItem(org.noorganization.instalist.model.ShoppingList, org.noorganization.instalist.model.Product)}.
     * @param _item A valid ListEntry, not null.
     * @return The changed item or null, if _item was invalid.
     */
    ListEntry strikeItem(ListEntry _item);

    /**
     * Alias for {@link #unstrikeItem(org.noorganization.instalist.model.ShoppingList, org.noorganization.instalist.model.Product)}.
     * @param _item A valid ListEntry, not null.
     * @return The changed item or null if _item was invalid.
     */
    ListEntry unstrikeItem(ListEntry _item);

    boolean removeItem(ShoppingList _list, Product _product);
    boolean removeItem(ListEntry _item);

    /**
     * Creates a list and returns it.
     * @param _name The name of the new list. Not null, not empty.
     * @return The new list or null, if creation failed.
     */
    ShoppingList addList(String _name);
    boolean removeList(ShoppingList _list);

    /**
     * Renames a list and returns it. The name must be unique or the rename will fail.
     * @param _list A valid ShoppingList, not null.
     * @param _newName A new name for the list. Not null, not empty.
     * @return The modified list or the old list.
     */
    ShoppingList renameList(ShoppingList _list, String _newName);
}
