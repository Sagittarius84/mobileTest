package org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.implementation;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.listadapter.PlainShoppingListOverviewAdapter;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.IShoppingListHelper;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.MenuStates;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.IContextItemClickedHelper;

/**
 * Helper for handdling the PlainShoppingList in the sidebar.
 * Created by tinos_000 on 25.06.2015.
 */
public class PlainShoppingListHelper implements IShoppingListHelper {

    private PlainShoppingListOverviewAdapter mListAdapter;
    private ListView mListView;
    private Context mContext;
    private IContextItemClickedHelper mViewHelper;
    private IBaseActivity mBaseActivityInterface;

    private boolean mIsActive;

    public PlainShoppingListHelper(Context _Context, IBaseActivity _BaseActivityInterface, ListView _ListView){
        mListView   = _ListView;
        mContext    = _Context;
        mViewHelper = new ContextItemClickedHelper(_Context);

        mBaseActivityInterface = _BaseActivityInterface;
        updateAdapter();
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
        if(_IsActive) {
            mListView.setVisibility(View.VISIBLE);
            mBaseActivityInterface.registerForContextMenu(mListView);
        } else{
            mListView.setVisibility(View.GONE);
            mBaseActivityInterface.unregisterForContextMenu(mListView);
        }
    }

    @Override
    public void addCategory(Category _Category) {
        return;
    }

    @Override
    public void updateCategory(Category _Category) {
        return;
    }

    @Override
    public void removeCategory(Category _Category) {
        return;
    }

    @Override
    public void addList(ShoppingList _ShoppingList) {
        mListAdapter.addList(_ShoppingList);
    }

    @Override
    public void updateList(ShoppingList _ShoppingList) {
        mListAdapter.updateList(_ShoppingList);
    }

    @Override
    public void removeList(ShoppingList _ShoppingList) {
        mListAdapter.removeList(_ShoppingList);
    }

    @Override
    public void updateAdapter() {
        mListAdapter = new PlainShoppingListOverviewAdapter(mContext, ShoppingList.listAll(ShoppingList.class));
        mListView.setAdapter(mListAdapter);
    }
}