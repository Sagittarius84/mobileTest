package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;

import java.util.List;


/**
 * Implementation of {@link org.noorganization.instalist.controller.IListController} as
 * singleton. Please retrieve your instance per {@link #getInstance()}.
 */
public class ListController implements IListController {

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
        if (_list == null || _product == null) {
            return null;
        }

        ShoppingList savedList = SugarRecord.findById(ShoppingList.class, _list.getId());
        Product savedProduct = SugarRecord.findById(Product.class, _product.getId());
        if (savedList == null || savedProduct == null) {
            return null;
        }

        ListEntry item = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(savedList.getId()),
                Condition.prop("m_product").eq(savedProduct.getId())).first();
        if (item == null) {
            if (_amount < 0.001f) {
                return null;
            }
            item = new ListEntry(_list, _product, _amount);
        } else {
            if (_amount < 0.001f) {
                return item;
            }
            item.mAmount = _amount;
        }
        item.save();

        return item;
    }

    @Override
    public void strikeAllItems(ShoppingList _list) {
        if (_list == null) {
            return;
        }
        ShoppingList toChange = SugarRecord.findById(ShoppingList.class, _list.getId());
        if (toChange == null) {
            return;
        }

        List<ListEntry> entries = toChange.getEntries();
        for (ListEntry entry : entries) {
            entry.mStruck = true;
        }
        SugarRecord.saveInTx(entries);
    }

    @Override
    public void unstrikeAllItems(ShoppingList _list) {
        if (_list == null) {
            return;
        }
        ShoppingList toChange = SugarRecord.findById(ShoppingList.class, _list.getId());
        if (toChange == null) {
            return;
        }

        List<ListEntry> entries = toChange.getEntries();
        for (ListEntry entry : entries) {
            entry.mStruck = false;
        }
        SugarRecord.saveInTx(entries);
    }

    @Override
    public ListEntry strikeItem(ShoppingList _list, Product _product) {
        if (_list == null || _product == null) {
            return null;
        }

        ListEntry toChange = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(_list.getId()),
                Condition.prop("m_product").eq(_product.getId())).first();
        return strikeItem(toChange, false);
    }

    @Override
    public ListEntry unstrikeItem(ShoppingList _list, Product _product) {
        if (_list == null || _product == null) {
            return null;
        }

        ListEntry toChange = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(_list.getId()),
                Condition.prop("m_product").eq(_product.getId())).first();
        return unstrikeItem(toChange, false);
    }

    private ListEntry unstrikeItem(ListEntry _toChange, boolean _reload) {
        if (_toChange == null) {
            return null;
        }

        ListEntry rtn = (_reload ? SugarRecord.findById(ListEntry.class,_toChange.getId()) : _toChange);
        rtn.mStruck = true;
        rtn.save();

        return rtn;
    }

    private ListEntry strikeItem(ListEntry _item, boolean _reload) {
        if (_item == null) {
            return null;
        }

        ListEntry rtn = (_reload ? SugarRecord.findById(ListEntry.class,_item.getId()) : _item);
        rtn.mStruck = true;
        rtn.save();

        return rtn;
    }

    @Override
    public ListEntry strikeItem(ListEntry _item) {
        return strikeItem(_item, true);
    }

    @Override
    public ListEntry unstrikeItem(ListEntry _item) {
        return unstrikeItem(_item, true);
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
