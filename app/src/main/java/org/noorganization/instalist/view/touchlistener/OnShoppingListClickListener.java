package org.noorganization.instalist.view.touchlistener;

import android.view.View;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Handles the clicks on a ShoppingList item.
 * Created by TS on 20.06.2015.
 */
public class OnShoppingListClickListener implements View.OnClickListener {

    private ShoppingList mShoppingList;
    private IOnShoppingListClickListenerEvents mOnShoppingListClickEvent;

    /**
     * Constructor of OnShoppingListClickListener.
     * @param _IOnShoppingListClickEvent The interface for handling ShoppingList clicks.
     * @param _ShoppingList The ShoppingList that is handled when a click on it occurs.
     */
    public OnShoppingListClickListener(IOnShoppingListClickListenerEvents _IOnShoppingListClickEvent, ShoppingList _ShoppingList){
        mShoppingList = _ShoppingList;
        mOnShoppingListClickEvent = _IOnShoppingListClickEvent;
    }

    @Override
    public void onClick(View v) {
        mOnShoppingListClickEvent.onShoppingListClicked(mShoppingList);
    }
}
