package org.noorganization.instalist.presenter.event;

import android.support.annotation.NonNull;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Sent when a ShoppingList entry was selected.
 * Created by tinos_000 on 24.11.2015.
 */
public class ShoppingListSelectedMessage {

    /**
     * The attribute ShoppingList.
     */
    public ShoppingList mShoppingList;

    /**
     * Constructor of the EventMessage.
     * @param _shoppingList the shoppingList that was selected.
     */
    public ShoppingListSelectedMessage(@NonNull ShoppingList _shoppingList) {
        mShoppingList = _shoppingList;
    }
}
