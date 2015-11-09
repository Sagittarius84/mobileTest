package org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.implementation;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.touchlistener.sidebar.OnCancelClickListenerWithData;
import org.noorganization.instalist.presenter.touchlistener.sidebar.OnSubmitClickListenerWithParentData;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.listadapter.ExpandableCategoryItemListAdapter;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.MenuStates;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.IContextItemClickedHelper;
import org.noorganization.instalist.presenter.sidedrawermodelwrapper.helper.IShoppingListHelper;
import org.noorganization.instalist.presenter.utils.PreferencesManager;

/**
 * Helper for Expandable ShoppingList.
 * Created by tinos_000 on 25.06.2015.
 */
public class ExpandableShoppingListHelper implements IShoppingListHelper {

    private ExpandableCategoryItemListAdapter mExpandableListAdapter;
    private ExpandableListView                mExpandableListView;
    private Context                           mContext;
    private IContextItemClickedHelper         mContextItemClickedHelper;
    private IBaseActivity                     mBaseActivity;

    private boolean mIsActive;

    public ExpandableShoppingListHelper(Context _Context, IBaseActivity _BaseActivityInterface,
                                        ExpandableListView _ExpandableListView) {
        mContext = _Context;
        mBaseActivity = _BaseActivityInterface;
        mExpandableListView = _ExpandableListView;
        mContextItemClickedHelper = new ContextItemClickedHelper(_Context);
        updateAdapter();
    }

    @Override
    public ContextMenu createContextMenu(ContextMenu _Menu, View _View, ContextMenu.ContextMenuInfo _MenuInfo) {
        ExpandableListView.ExpandableListContextMenuInfo contextMenuInfo =
                (ExpandableListView.ExpandableListContextMenuInfo) _MenuInfo;

        int entityType    = ExpandableListView.getPackedPositionType(contextMenuInfo.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(contextMenuInfo.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(contextMenuInfo.packedPosition);

        switch (entityType) {
            case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                _Menu.setHeaderTitle(mContext.getString(R.string.category_action));
                _Menu.add(MenuStates.GROUP_MENU, MenuStates.GROUP_MENU_ADD_LIST_ACTION, 1, mContext.getString(R.string.add_list));
                _Menu.add(MenuStates.GROUP_MENU, MenuStates.GROUP_MENU_REMOVE_CATEGORY_ACTION, 2, mContext.getString(R.string.remove_category));
                _Menu.add(MenuStates.GROUP_MENU, MenuStates.GROUP_MENU_EDIT_CATEGORY_ACTION, 3, mContext.getString(R.string.edit_category_name));
                break;
            case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                _Menu.setHeaderTitle(mContext.getString(R.string.shopping_list_action));
                _Menu.add(MenuStates.CHILD_MENU, MenuStates.CHILD_MENU_EDIT_LIST_NAME_ACTION, 1, mContext.getString(R.string.edit_shopping_list));
                _Menu.add(MenuStates.CHILD_MENU, MenuStates.CHILD_MENU_REMOVE_LIST_ACTION, 2, mContext.getString(R.string.remove_shopping_list));
                _Menu.add(MenuStates.CHILD_MENU, MenuStates.CHILD_MENU_MOVE_TO_CATEGORY_ACTION, 3, mContext.getString(R.string.move_category));
                break;
            default:
                break;
        }
        return _Menu;
    }

    @Override
    public void onContextMenuItemClicked(MenuItem _Item) {
        int groupPosition,
                childPosition,
                entityType,
                itemId,
                flatPosition,
                firstVisiblePosition;

        long defaultCategoryId;

        ExpandableListView.ExpandableListContextMenuInfo contextMenuInfo;
        View                                             view;
        ViewSwitcher                                     viewSwitcher;

        itemId = _Item.getItemId();

        contextMenuInfo = (ExpandableListView.ExpandableListContextMenuInfo) _Item.getMenuInfo();
        entityType = ExpandableListView.getPackedPositionType(contextMenuInfo.packedPosition);
        groupPosition = ExpandableListView.getPackedPositionGroup(contextMenuInfo.packedPosition);
        childPosition = ExpandableListView.getPackedPositionChild(contextMenuInfo.packedPosition);

        flatPosition = mExpandableListView.getFlatListPosition(contextMenuInfo.packedPosition);
        firstVisiblePosition = mExpandableListView.getFirstVisiblePosition();

        view = mExpandableListView.getChildAt(flatPosition - firstVisiblePosition);
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.expandable_list_view_view_switcher);

        // fetch it in here, because it can change over time.
        defaultCategoryId = PreferencesManager.getInstance().getLongValue(PreferencesManager.KEY_DEFAULT_CATEGORY_ID);

        switch (_Item.getGroupId()) {
            case MenuStates.GROUP_MENU:
                //region GROUP_MENU
                Category category = (Category) mExpandableListAdapter.getGroup(groupPosition);
                String categoryName = category.mName;

                switch (itemId) {
                    case MenuStates.GROUP_MENU_ADD_LIST_ACTION:
                        Toast.makeText(mContext, "Add list on category: " + categoryName, Toast.LENGTH_SHORT).show();
                        ((IBaseActivity) mContext).setSideDrawerAddListButtonListener(category.getId());
                        break;
                    case MenuStates.GROUP_MENU_REMOVE_CATEGORY_ACTION:
                        Toast.makeText(mContext, "Remove group: " + categoryName, Toast.LENGTH_SHORT).show();
                        if (category.getId() == defaultCategoryId) {
                            Toast.makeText(mContext, mContext.getString(R.string.delete_category_error_category_is_default), Toast.LENGTH_LONG).show();
                        } else {
                            ControllerFactory.getCategoryController().removeCategory(category);
                            mBaseActivity.removeCategory(category);
                        }
                        break;
                    case MenuStates.GROUP_MENU_EDIT_CATEGORY_ACTION:
                        //region Edit Category name
                        Toast.makeText(mContext, "Rename category: " + categoryName, Toast.LENGTH_SHORT).show();

                        final EditText editText;
                        ImageView cancelView, submitView;

                        cancelView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_cancel);
                        submitView = (ImageView) view.findViewById(R.id.expandable_list_view_edit_submit);

                        editText = (EditText) view.findViewById(R.id.expandable_list_view_category_name_edit);

                        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
                        submitView.setOnClickListener(new OnSubmitClickListenerWithParentData(viewSwitcher, editText, category.getId(), mExpandableListAdapter));

                        editText.setText(category.mName);

                        viewSwitcher.showNext();
                        //endregion
                        break;
                }
                //endregion GROUP_MENU
                break;
            case MenuStates.CHILD_MENU:
                //region CHILD_MENU
                Category categoryForShoppingList;
                ShoppingList shoppingList;
                EditText editText;
                ImageView cancelView,
                        submitView;

                shoppingList = (ShoppingList) mExpandableListAdapter.getChild(groupPosition, childPosition);
                categoryForShoppingList = (Category) mExpandableListAdapter.getGroup(groupPosition);

                // get the shoppinglist from database
                shoppingList = ShoppingList.findById(ShoppingList.class, shoppingList.getId());
                switch (itemId) {
                    case MenuStates.CHILD_MENU_EDIT_LIST_NAME_ACTION:
                        mContextItemClickedHelper.editListName(view, shoppingList, viewSwitcher);
                        break;
                    case MenuStates.CHILD_MENU_REMOVE_LIST_ACTION:
                        mContextItemClickedHelper.removeList(shoppingList);
                        break;
                    case MenuStates.CHILD_MENU_MOVE_TO_CATEGORY_ACTION:
                        mContextItemClickedHelper.changeCategoryOfList(view, shoppingList, categoryForShoppingList, viewSwitcher);
                        break;
                }
                //endregion CHILD_MENU
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
        if (_IsActive) {
            mExpandableListView.setVisibility(View.VISIBLE);
            mBaseActivity.registerForContextMenu(mExpandableListView);
        } else {
            mExpandableListView.setVisibility(View.GONE);
            mBaseActivity.unregisterForContextMenu(mExpandableListView);
        }
    }

    @Override
    public void addCategory(Category _Category) {
        mExpandableListAdapter.addCategory(_Category);
    }

    @Override
    public void updateCategory(Category _Category) {
        mExpandableListAdapter.updateCategory(_Category);
    }

    @Override
    public void removeCategory(Category _Category) {
        mExpandableListAdapter.removeCategory(_Category);
    }

    @Override
    public void addList(ShoppingList _ShoppingList) {
        mExpandableListAdapter.notifyShoppingListChanged();
        return;
    }

    @Override
    public void updateList(ShoppingList _ShoppingList) {
        mExpandableListAdapter.notifyShoppingListChanged();
        return;
    }

    @Override
    public void removeList(ShoppingList _ShoppingList) {
        mExpandableListAdapter.notifyShoppingListChanged();
        return;
    }

    @Override
    public void updateAdapter() {
        mExpandableListAdapter = new ExpandableCategoryItemListAdapter(mContext, Category.listAll(Category.class));
        mExpandableListView.setAdapter(mExpandableListAdapter);
    }
}
