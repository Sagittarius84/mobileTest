package org.noorganization.instalist.controller.implementation;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.ListModificationListener;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;


/**
 * Implementation of {@link org.noorganization.instalist.controller.ListModificationListener} as
 * singleton. Please retrieve your instance per {@link #getInstance()}.
 */
public class ListController implements ListModificationListener {

    private static ListController mInstance;

    private ListController() {
    }

    public static ListController getInstance() {
        if (mInstance == null) {
            mInstance = new ListController();
        }

        return mInstance;
    }


    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount) {
        if (_list == null || _product == null || _amount < 0.001f) {
            return null;
        }

        ListEntry item = Select.from(ListEntry.class).where(Condition.prop("m_list").
                eq(_list.getId())).first();
        if (item == null) {
            item = new ListEntry(_list, _product, _amount);
        }

        item.mAmount = _amount;
        item.save();

        return item;
    }

    @Override
    public boolean removeItem(ShoppingList _list, Product _product) {
        if (_list == null || _product == null) {
            return false;
        }

        ListEntry toDelete = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(_list.getId()),
                Condition.prop("m_product").eq(_product.getId())).first();

        return removeItem(toDelete);
    }

    @Override
    public boolean removeItem(ListEntry _item) {
        if (_item == null) {
            return false;
        }

        _item.delete();

         long deletedEntryCount = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(_item.mList.getId()),
                Condition.prop("m_product").eq(_item.mProduct.getId())).count();

        return deletedEntryCount == 0;
    }

    @Override
    public ShoppingList addList(String _name) {
        if (_name == null || _name.length() == 0 || existsListName(_name)) {
            return null;
        }

        ShoppingList rtn = new ShoppingList(_name);
        rtn.save();

        return rtn;
    }

    @Override
    public boolean removeList(ShoppingList _list) {
        if (_list == null) {
            return false;
        }

        long countOfLinksToList = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(_list.getId())).count();

        if (countOfLinksToList > 0) {
            return false;
        }

        Long oldId = _list.getId();
        _list.delete();

        return ShoppingList.findById(ShoppingList.class, oldId) == null;
    }

    @Override
    public ShoppingList renameList(ShoppingList _list, String _newName) {
        if (_list == null || _newName == null || _newName.length() == 0 ||
                existsListName(_newName)) {
            return _list;
        }

        ShoppingList rtn = ShoppingList.findById(ShoppingList.class, _list.getId());
        rtn.mName = _newName;
        rtn.save();

        return rtn;
    }

    private boolean existsListName(String _name) {
        long existingListWithSameNameCount = Select.from(ShoppingList.class).where(
                Condition.prop("m_name").eq(_name)).count();

        return (existingListWithSameNameCount > 0);
    }
}
