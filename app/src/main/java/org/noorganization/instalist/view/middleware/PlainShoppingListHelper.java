package org.noorganization.instalist.view.middleware;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.listadapter.PlainShoppingListOverviewAdapter;
import org.noorganization.instalist.view.middleware.helper.IContextItemClickedHelper;
import org.noorganization.instalist.view.middleware.helper.implementation.ContextItemClickedHelper;

/**
 * Created by tinos_000 on 25.06.2015.
 */
public class PlainShoppingListHelper implements IShoppingListHelper {

    private PlainShoppingListOverviewAdapter mListAdapter;
    private ListView mListView;
    private Context mContext;
    private IContextItemClickedHelper mViewHelper;
    private boolean mIsActive;

    public PlainShoppingListHelper(PlainShoppingListOverviewAdapter _ListAdapter, ListView _ListView, Context _Context){
        mListAdapter = _ListAdapter;
        mListView   = _ListView;
        mContext    = _Context;
        mViewHelper = new ContextItemClickedHelper(_Context);
    }

    @Override
    public ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo) {
        _Menu.setHeaderTitle(mContext.getString(R.string.shopping_list_action));
        _Menu.add(MenuStates.PLAIN_SHOPPINGLIST_MENU, MenuStates.PLAIN_SHOPPINGLIST_EDIT_LIST_NAME_ACTION, 1, mContext.getString(R.string.edit_shopping_list));
        _Menu.add(MenuStates.PLAIN_SHOPPINGLIST_MENU, MenuStates.PLAIN_SHOPPINGLIST_REMOVE_LIST_ACTION, 2, mContext.getString(R.string.remove_shopping_list));
        return _Menu;
    }

    @Override
    public void onContextMenuItemClicked(MenuItem _Item) {
        int itemId;

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = null;
        ViewSwitcher viewSwitcher;
        View view;

        adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) _Item.getMenuInfo();

        itemId = _Item.getItemId();
        view = adapterContextMenuInfo.targetView;
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.expandable_list_view_view_switcher);

        switch (_Item.getGroupId()) {
            case MenuStates.PLAIN_SHOPPINGLIST_MENU:
                ShoppingList shoppingList1 = mListAdapter.getItem(adapterContextMenuInfo.position);
                switch (itemId) {
                    case MenuStates.PLAIN_SHOPPINGLIST_EDIT_LIST_NAME_ACTION:
                        mViewHelper.editListName(view, shoppingList1, viewSwitcher);
                        break;
                    case MenuStates.PLAIN_SHOPPINGLIST_REMOVE_LIST_ACTION:
                        mViewHelper.removeList(shoppingList1);
                        break;
                }
                break;

        }
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public void setActiveState(boolean _IsActive) {
        mIsActive = _IsActive;
        mListView.setVisibility(View.VISIBLE);
    }
}