package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.model.ShoppingList;

/**
 * Provides an Interface to the BaseActivity to control the current list.
 * Created by tinos_000 on 16.06.2015.
 */
public interface IBaseActivityListEvent {

     /**
      * Creates a new fragment with the listentries of the given ShoppingList item.
      *
      * @param _ShoppingList of the list that the content should be shown.
      */
    void selectList(ShoppingList _ShoppingList);
}
