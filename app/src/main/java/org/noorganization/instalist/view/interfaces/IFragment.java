package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Interface for registering Fragments for general events.
 * Created by TS on 04.07.2015.
 */
public interface IFragment {

    /**
     * Called when @Link{ShoppingList} has been removed.
     * @param _ShoppingList the @Link{ShoppingList} that has been removed.
     */
    void onShoppingListRemoved(ShoppingList _ShoppingList);
}
