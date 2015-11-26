package org.noorganization.instalist.view.touchlistener;

import org.noorganization.instalist.model.ShoppingList;

/**
 * The Events that needs to be implemented when a ShoppingList is being clicked.
 * Created by TS on 20.06.2015.
 */
public interface IOnShoppingListClickListenerEvents {

    /**
     * Called when a ShoppingList was clicked.
     * @param _ShoppingList the ShoppingList object that was clicked.
     */
    void onShoppingListClicked(ShoppingList _ShoppingList);
}
