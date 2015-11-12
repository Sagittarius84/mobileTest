package org.noorganization.instalist.controller.implementation;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.controller.event.ListChangedMessage;
import org.noorganization.instalist.controller.event.ListItemChangedMessage;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.provider.InstalistProvider;

import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Implementation of {@link org.noorganization.instalist.controller.IListController} as
 * singleton. Please retrieve your instance per {@link #getInstance()}.
 */
public class ListController implements IListController {

    private static ListController mInstance;

    private EventBus            mBus;
    private Context             mContext;
    private IProductController  mProductController;
    private ICategoryController mCategoryController;
    private ContentResolver     mResolver;

    private ListController(Context _context) {
        mBus = EventBus.getDefault();
        mContext = _context;
        mProductController = ControllerFactory.getProductController();
        mCategoryController = ControllerFactory.getCategoryController();
        mResolver = mContext.getContentResolver();
    }

    static ListController getInstance(Context _context) {
        if (mInstance == null) {
            mInstance = new ListController(_context);
        }

        return mInstance;
    }

    private ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount,
                                      boolean _prioUsed, int _prio, boolean _addAmount) {
        if (_list == null || _product == null) {
            return null;
        }

        Cursor listCheck = mResolver.query(
                _list.toUri(InstalistProvider.BASE_CONTENT_URI),
                new String[]{ShoppingList.COLUMN.ID},
                null, null, null);
        if (listCheck == null) {
            return null;
        } else if (listCheck.getCount() != 1) {
            listCheck.close();
            return null;
        }
        listCheck.close();
        Cursor productCheck = mResolver.query(
                _product.toUri(InstalistProvider.BASE_CONTENT_URI),
                new String[]{Product.COLUMN.ID},
                null, null, null);
        if (productCheck == null) {
            return null;
        } else if (productCheck.getCount() != 1) {
            productCheck.close();
            return null;
        }
        productCheck.close();

        Uri listUri = _list.toUri(InstalistProvider.BASE_CONTENT_URI);
        Cursor entryCheck = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, listUri.getPath() + "/entry"),
                new String[]{ListEntry.COLUMN.ID},
                ListEntry.PREFIXED_COLUMN.PRODUCT + " = ?",
                new String[]{_product.id},
                null);
        ListEntry item = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(savedList.getId()),
                Condition.prop("m_product").eq(savedProduct.getId())).first();
        Change changeMade;
        if (item == null) {
            if (_amount < 0.001f) {
                return null;
            }
            item = new ListEntry(savedList, savedProduct, _amount, false, (_prioUsed ? _prio : 0));
            item.save();

            changeMade = Change.CREATED;
        } else {
            if (_amount < 0.001f) {
                return item;
            }
            item.mAmount = (_addAmount ? item.mAmount : 0.0f) + _amount;
            if (_prioUsed) {
                item.mPriority = _prio;
            }
            item.save();

            changeMade = Change.CHANGED;
        }

        mBus.post(new ListItemChangedMessage(changeMade, item));

        return item;
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount) {
        return addOrChangeItem(_list, _product, _amount, false, 0, false);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, int _prio) {
        return addOrChangeItem(_list,_product, _amount, true, _prio, false);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, boolean _addAmount) {
        return addOrChangeItem(_list, _product, _amount, false, 0, _addAmount);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, int _prio,
                                     boolean _addAmount) {
        return addOrChangeItem(_list,_product, _amount, true, _prio, true);
    }

    @Override
    public ListEntry getEntryById(@NonNull String _UUID) {
        Cursor entryCursor = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"),
                ListEntry.COLUMN.ALL_COLUMNS,
                ListEntry.COLUMN.ID + " = ?",
                new String[]{_UUID},
                null);
        if (entryCursor == null) {
            Log.e(getClass().getCanonicalName(), "Searching ListEntry by UUID resulted null. " +
                    "Returning no ListEntry.");
            return null;
        }
        if (entryCursor.getCount() == 0) {
            entryCursor.close();
            return null;
        }
        entryCursor.moveToFirst();
        ListEntry rtn = new ListEntry();
        rtn.mUUID = entryCursor.getString(entryCursor.getColumnIndex(ListEntry.COLUMN.ID));
        rtn.mList = getListById(entryCursor.getString(entryCursor.getColumnIndex(
                ListEntry.COLUMN.LIST)));
        rtn.mProduct = mProductController.getProductById(entryCursor.getString(
                entryCursor.getColumnIndex(ListEntry.COLUMN.PRODUCT)));
        rtn.mAmount = entryCursor.getFloat(entryCursor.getColumnIndex(ListEntry.COLUMN.AMOUNT));
        rtn.mPriority = entryCursor.getInt(entryCursor.getColumnIndex(ListEntry.COLUMN.PRIORITY));
        rtn.mStruck = (entryCursor.getInt(entryCursor.getColumnIndex(ListEntry.COLUMN.STRUCK)) != 0);
        return rtn;
    }

    @Override
    public ShoppingList getListById(@NonNull String _UUID) {
        Cursor entryCursor = mContext.getContentResolver().query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"),
                ShoppingList.COLUMN.ALL_COLUMNS,
                ShoppingList.COLUMN.ID + " = ?",
                new String[] { _UUID },
                null);
        if (entryCursor == null) {
            Log.e(getClass().getCanonicalName(), "Searching ListEntry by UUID resulted null. " +
                    "Returning no ListEntry.");
            return null;
        }
        if (entryCursor.getCount() == 0) {
            entryCursor.close();
            return null;
        }
        entryCursor.moveToFirst();
        ShoppingList rtn = new ShoppingList();
        rtn.mUUID = entryCursor.getString(entryCursor.getColumnIndex(ShoppingList.COLUMN.ID));
        rtn.mName = entryCursor.getString(entryCursor.getColumnIndex(ShoppingList.COLUMN.NAME));
        rtn.mCategory = mCategoryController.getCategoryByID(entryCursor.getString(
                entryCursor.getColumnIndex(ShoppingList.COLUMN.CATEGORY)));
        return rtn;
    }

    @Override
    public void strikeAllItems(ShoppingList _list) {
        if (_list == null || _list.mUUID == null) {
            return;
        }
        Cursor itemsToStrike = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        _list.toUri(InstalistProvider.BASE_CONTENT_URI).getPath() + "/entry"),
                new String[]{ ListEntry.COLUMN.ID },
                null, null, null);
        if (itemsToStrike != null) {
            itemsToStrike.moveToFirst();
            ContentValues strikeCV = new ContentValues(1);
            strikeCV.put(ListEntry.COLUMN.STRUCK, true);
            String prefixPath = _list.toUri(InstalistProvider.BASE_CONTENT_URI).getPath() + "/entry/";
            while (!itemsToStrike.isAfterLast()) {
                String entryUUID = itemsToStrike.getString(itemsToStrike.getColumnIndex(
                        ListEntry.COLUMN.ID));
                if(mResolver.update(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        prefixPath + entryUUID), strikeCV, null, null) == 1) {
                    mBus.post(new ListItemChangedMessage(Change.CHANGED, getEntryById(entryUUID)));
                }
                itemsToStrike.moveToNext();
            }
            itemsToStrike.close();
        }
    }

    @Override
    public void unstrikeAllItems(ShoppingList _list) {
        if (_list == null || _list.mUUID == null) {
            return;
        }
        Cursor itemsToStrike = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        _list.toUri(InstalistProvider.BASE_CONTENT_URI).getPath() + "/entry"),
                new String[]{ListEntry.COLUMN.ID},
                null, null, null);
        if (itemsToStrike != null) {
            itemsToStrike.moveToFirst();
            ContentValues strikeCV = new ContentValues(1);
            strikeCV.put(ListEntry.COLUMN.STRUCK, false);
            String prefixPath = _list.toUri(InstalistProvider.BASE_CONTENT_URI).getPath() + "/entry/";
            while (!itemsToStrike.isAfterLast()) {
                String entryUUID = itemsToStrike.getString(itemsToStrike.getColumnIndex(
                        ListEntry.COLUMN.ID));
                if(mResolver.update(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        prefixPath + entryUUID), strikeCV, null, null) == 1) {
                    mBus.post(new ListItemChangedMessage(Change.CHANGED, getEntryById(entryUUID)));
                }
                itemsToStrike.moveToNext();
            }
            itemsToStrike.close();
        }
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
        rtn.mStruck = false;
        rtn.save();

        mBus.post(new ListItemChangedMessage(Change.CHANGED, rtn));

        return rtn;
    }

    private ListEntry strikeItem(ListEntry _item, boolean _reload) {
        if (_item == null) {
            return null;
        }

        ListEntry rtn = (_reload ? SugarRecord.findById(ListEntry.class,_item.getId()) : _item);
        rtn.mStruck = true;
        rtn.save();

        mBus.post(new ListItemChangedMessage(Change.CHANGED, rtn));

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

        Long listId = _item.mList.getId();
        Long productId = _item.mProduct.getId();
        _item.delete();

        mBus.post(new ListItemChangedMessage(Change.DELETED, _item));

        long deletedEntryCount = Select.from(ListEntry.class).where(
                Condition.prop("m_list").eq(listId),
                Condition.prop("m_product").eq(productId)).count();

        return deletedEntryCount == 0;
    }

    @Override
    public ListEntry setItemPriority(ListEntry _item, int _newPrio) {
        if (_item == null) {
            return null;
        }

        ListEntry toChange = SugarRecord.findById(ListEntry.class, _item.getId());
        if (toChange == null) {
            return null;
        }

        toChange.mPriority = _newPrio;
        toChange.save();

        mBus.post(new ListItemChangedMessage(Change.CHANGED, toChange));

        return toChange;
    }

    @Override
    public ShoppingList addList(String _name) {
        return addList(_name, null);
    }

    @Override
    public ShoppingList addList(String _name, Category _category) {
        if (_name == null || _name.length() == 0 || existsListName(_name)) {
            return null;
        }

        Category targetCategory = null;
        if (_category != null) {
            targetCategory = SugarRecord.findById(Category.class, _category.getId());
            if (targetCategory == null) {
                return null;
            }
        }

        ShoppingList rtn = new ShoppingList(_name, targetCategory);
        rtn.save();

        mBus.post(new ListChangedMessage(Change.CREATED, rtn));

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

        mBus.post(new ListChangedMessage(Change.DELETED, _list));

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

        mBus.post(new ListChangedMessage(Change.CHANGED, rtn));

        return rtn;
    }

    @Override
    public ShoppingList moveToCategory(ShoppingList _list, Category _category) {
        if (_list == null) {
            return null;
        }

        ShoppingList listToChange = SugarRecord.findById(ShoppingList.class, _list.getId());
        if (listToChange == null) {
            return null;
        }

        Category targetCategory = null;
        if (_category != null) {
            targetCategory = SugarRecord.findById(Category.class, _category.getId());
            if (targetCategory == null) {
                return listToChange;
            }
        }

        listToChange.mCategory = targetCategory;
        listToChange.save();

        mBus.post(new ListChangedMessage(Change.CHANGED, listToChange));

        return listToChange;
    }

    private boolean existsListName(String _name) {
        long existingListWithSameNameCount = Select.from(ShoppingList.class).where(
                Condition.prop("m_name").eq(_name)).count();

        return (existingListWithSameNameCount > 0);
    }
}
