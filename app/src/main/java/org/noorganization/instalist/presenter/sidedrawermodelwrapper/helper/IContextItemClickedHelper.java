package org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper;

import android.view.View;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

/**
 * Created by tinos_000 on 25.06.2015.
 */
public interface IContextItemClickedHelper {
    void editListName(View _View, ShoppingList _ShoppingList, ViewSwitcher _ViewSwitcher);
    void removeList(ShoppingList _ShoppingList);
    void changeCategoryOfList(View _View, ShoppingList _ShoppingList, Category _CategoryForShoppingList, ViewSwitcher _ViewSwitcher);
}
