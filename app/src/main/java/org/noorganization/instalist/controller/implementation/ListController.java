package org.noorganization.instalist.controller.implementation;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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

import de.greenrobot.event.EventBus;


/**
 * Implementation of {@link org.noorganization.instalist.controller.IListController} as
 * singleton. Please retrieve your instance per {@link ListController#getInstance(Context _context)}.
 */
public class ListController implements IListController {

    private static String LOG_TAG = ListController.class.getName();
    private static ListController mInstance;

    private EventBus mBus;
    private Context mContext;
    private IProductController mProductController;
    private ICategoryController mCategoryController;
    private ContentResolver mResolver;

    private ListController(Context _context) {
        mBus = EventBus.getDefault();
        mContext = _context;
        mProductController = ControllerFactory.getProductController();
        mCategoryController = ControllerFactory.getCategoryController();
        mResolver = mContext.getContentResolver();
    }

    public static ListController getInstance(Context _context) {
        if (mInstance == null) {
            mInstance = new ListController(_context);
        }

        return mInstance;
    }


    // region Public methods

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount) {
        return addOrChangeItem(_list, _product, _amount, false, 0, false);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, int _prio) {
        return addOrChangeItem(_list, _product, _amount, true, _prio, false);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, boolean _addAmount) {
        return addOrChangeItem(_list, _product, _amount, false, 0, _addAmount);
    }

    @Override
    public ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount, int _prio,
                                     boolean _addAmount) {
        return addOrChangeItem(_list, _product, _amount, true, _prio, true);
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
        rtn.mProduct = mProductController.findById(entryCursor.getString(
                entryCursor.getColumnIndex(ListEntry.COLUMN.PRODUCT)));
        rtn.mAmount = entryCursor.getFloat(entryCursor.getColumnIndex(ListEntry.COLUMN.AMOUNT));
        rtn.mPriority = entryCursor.getInt(entryCursor.getColumnIndex(ListEntry.COLUMN.PRIORITY));
        rtn.mStruck = (entryCursor.getInt(entryCursor.getColumnIndex(ListEntry.COLUMN.STRUCK)) != 0);
        return rtn;
    }

    @Override
    public ListEntry getEntryByListAndProduct(@NonNull ShoppingList _list, @NonNull Product _product) {
        Cursor entrySearch = mResolver.query(
                Uri.withAppendedPath(
                        InstalistProvider.BASE_CONTENT_URI,
                        _list.getUriPath() + "/entry"),
                new String[]{ListEntry.COLUMN.ID},
                ListEntry.COLUMN.PRODUCT + " = ?",
                new String[]{_product.mUUID},
                null);
        if (entrySearch == null) {
            Log.e(getClass().getCanonicalName(), "Search for ListEntry by product failed. " +
                    "Returning no found entry.");
            return null;
        } else if (entrySearch.getCount() == 0) {
            return null;
        }
        entrySearch.moveToFirst();
        ListEntry listEntry =  getEntryById(entrySearch.getString(entrySearch.getColumnIndex(
                ListEntry.COLUMN.ID)));
        entrySearch.close();
        return  listEntry;
    }

    @Override
    public ShoppingList getListById(@NonNull String _UUID) {
        Cursor entryCursor = mContext.getContentResolver().query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"),
                ShoppingList.COLUMN.ALL_COLUMNS,
                ShoppingList.COLUMN.ID + " = ?",
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
                new String[]{ListEntry.COLUMN.ID},
                null, null, null);
        if (itemsToStrike != null) {
            itemsToStrike.moveToFirst();
            ContentValues strikeCV = new ContentValues(1);
            strikeCV.put(ListEntry.COLUMN.STRUCK, true);
            String prefixPath = _list.toUri(InstalistProvider.BASE_CONTENT_URI).getPath() + "/entry/";
            while (!itemsToStrike.isAfterLast()) {
                String entryUUID = itemsToStrike.getString(itemsToStrike.getColumnIndex(
                        ListEntry.COLUMN.ID));
                if (mResolver.update(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
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
                if (mResolver.update(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
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

        ListEntry toChange = getEntryByListAndProduct(_list, _product);
        return strikeItem(toChange, false);
    }

    @Override
    public ListEntry unstrikeItem(ShoppingList _list, Product _product) {
        if (_list == null || _product == null) {
            return null;
        }

        ListEntry toChange = getEntryByListAndProduct(_list, _product);
        return unstrikeItem(toChange, false);
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

        ListEntry toDelete = getEntryByListAndProduct(_list, _product);
        return removeItem(toDelete);
    }

    @Override
    public boolean removeItem(ListEntry _item) {
        if (_item == null) {
            return false;
        }

        int deletedEntries = mResolver.delete(_item.toUri(InstalistProvider.BASE_CONTENT_URI), null,
                null);
        if (deletedEntries > 0) {
            mBus.post(new ListItemChangedMessage(Change.DELETED, _item));
            return true;
        }
        return false;
    }

    @Override
    public ListEntry setItemPriority(ListEntry _item, int _newPrio) {
        if (_item == null) {
            return null;
        }

        ContentValues entryUpdateCV = new ContentValues(1);
        entryUpdateCV.put(ListEntry.COLUMN.PRIORITY, _newPrio);
        int changedEntries = mResolver.update(_item.toUri(InstalistProvider.BASE_CONTENT_URI),
                entryUpdateCV, null, null);
        ListEntry entry = getEntryById(_item.mUUID);
        if (changedEntries > 0) {
            mBus.post(new ListItemChangedMessage(Change.CHANGED, entry));
        }

        return entry;
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

        ContentValues newListCV = new ContentValues(2);
        newListCV.put(ShoppingList.COLUMN.NAME, _name);

        String insertUri = InstalistProvider.BASE_CONTENT_URI.getPath().concat("category/").concat("?").concat("/list");
        if (_category == null) {
            // no category is selected, use default one
            newListCV.putNull(ShoppingList.COLUMN.CATEGORY);
            // set the category to the default uuid
            insertUri = String.format(insertUri, "-");
        } else {
            // category is set by uuid
            newListCV.put(ShoppingList.COLUMN.CATEGORY, _category.mUUID);
            insertUri = String.format(insertUri, _category.mUUID);
        }

        // add list to db
        // TODO this should do it if I understand your intention correct
        Uri createdList = mResolver.insert(Uri.parse(insertUri), newListCV);

        if (createdList == null) {
            return null;
        }

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.mUUID = createdList.getLastPathSegment();
        shoppingList.mCategory = _category;
        mBus.post(new ListChangedMessage(Change.CREATED, shoppingList));

        return shoppingList;
    }

    @Override
    public boolean removeList(ShoppingList _list) {
        if (_list == null) {
            return false;
        }

        int deletedListCount = mResolver.delete(Uri.parse(_list.getUriPath()), null, null);

        if (deletedListCount > 0) {
            mBus.post(new ListChangedMessage(Change.DELETED, _list));
            return true;
        }

        Cursor cursor = mResolver.query(_list.toUri(InstalistProvider.BASE_CONTENT_URI), ShoppingList.COLUMN.ALL_COLUMNS, null, null, null);

        boolean rtn = cursor != null && cursor.getCount() == 0;
        if(cursor != null){
            cursor.close();
        }
        return rtn;
    }

    @Override
    public ShoppingList renameList(ShoppingList _list, String _newName) {
        if (_list == null || _newName == null || _newName.length() == 0 ||
                existsListName(_newName)) {
            return _list;
        }

        String oldName = _list.mName;
        _list.mName = _newName;
        int affectedRows = mResolver.update(_list.toUri(InstalistProvider.BASE_CONTENT_URI), _list.toContentValues(), null, null);

        if (affectedRows > 0) {
            mBus.post(new ListChangedMessage(Change.CHANGED, _list));
        } else {
            _list.mName = oldName;
        }

        return _list;
    }

    @Override
    public ShoppingList moveToCategory(ShoppingList _list, Category _category) {
        if (_list == null) {
            return null;
        }

        Cursor listCursor = mResolver.query(Uri.parse(_list.getUriPath()), ShoppingList.COLUMN.ALL_COLUMNS, null, null, null);
        if (listCursor == null || listCursor.getCount() == 0) {
            return null;
        }

        listCursor.moveToFirst();

        // assign the values new to prevent some name changes and to get a consistent state
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.mUUID = listCursor.getString(listCursor.getColumnIndex(ShoppingList.COLUMN.ID));
        shoppingList.mName = listCursor.getString(listCursor.getColumnIndex(ShoppingList.COLUMN.NAME));
        shoppingList.mCategory = _list.mCategory;


        Cursor categoryCursor;
        Category category;

        if (_category != null) {
            categoryCursor = mResolver.query(_category.toUri(InstalistProvider.BASE_CONTENT_URI), Category.COLUMN.ALL_COLUMNS, null, null, null);
            if (categoryCursor == null || categoryCursor.getCount() == 0) {
                return shoppingList;
            }

            categoryCursor.moveToFirst();
            category = new Category();
            category.mUUID = categoryCursor.getString(categoryCursor.getColumnIndex(Category.COLUMN.ID));
            category.mName = categoryCursor.getString(categoryCursor.getColumnIndex(Category.COLUMN.NAME));
            shoppingList.mCategory = category;

            categoryCursor.close();
        }

        listCursor.close();

        int affectedRows = mResolver.update(shoppingList.toUri(InstalistProvider.BASE_CONTENT_URI), shoppingList.toContentValues(), null, null);
        if (affectedRows > 0) {
            mBus.post(new ListChangedMessage(Change.CHANGED, shoppingList));
        }

        return shoppingList;
    }


    //endregion public methods

    //region Private methods

    /**
     * Checks if an list with the given name already exists in the database.
     *
     * @param _name the name to be checked.
     * @return true if there is an exisiting one else false.
     */
    private boolean existsListName(String _name) {
        Uri requestUri = Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list");
        Cursor listCursor = mResolver.query(requestUri, new String[]{ShoppingList.COLUMN.NAME}, ShoppingList.COLUMN.NAME + "= ?", new String[]{_name}, null);

        if (listCursor == null) {
            Log.e(LOG_TAG, "Internal failure while querying a list name in method existsListName(String _name).");
            // return true just to prevent errors that can result from this.
            return true;
        }
        boolean rtn = listCursor.getCount() > 0;
        listCursor.close();
        return rtn;
    }

    /**
     * Adds or updates a Listentry to a given {@link ShoppingList}.
     * // TODO: further description needed
     * @param _list the list where the item should be added or updated.
     * @param _product the product to insert/update.
     * @param _amount the new amount of the product in the given list.
     * @param _prioUsed flag that describes if the prio is used.
     * @param _prio the prio that will be assigned.
     * @param _addAmount the standard amount to add when hitting the inc/dec buttons.
     * @return the new listentry or null if some failure happened.
     */
    private ListEntry addOrChangeItem(ShoppingList _list, Product _product, float _amount,
                                      boolean _prioUsed, int _prio, boolean _addAmount) {
        if (_list == null || _product == null) {
            return null;
        }

        ShoppingList savedList = getListById(_list.mUUID);
        Product savedProduct = mProductController.findById(_product.mUUID);
        if (savedList == null || savedProduct == null) {
            return null;
        }

        Uri listUri = savedList.toUri(InstalistProvider.BASE_CONTENT_URI);
        Cursor entryCheck = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, listUri.getPath() + "/entry"),
                new String[]{ListEntry.COLUMN.ID, ListEntry.COLUMN.AMOUNT},
                ListEntry.PREFIXED_COLUMN.PRODUCT + " = ?",
                new String[]{_product.mUUID},
                null);
        if (entryCheck == null) {
            Log.e(getClass().getCanonicalName(), "Searching for existing ListEntry (to change it) " +
                    "failed. Returning no ListEntry.");
            return null;
        }
        ContentValues newEntryCV = new ContentValues(5);
        newEntryCV.put(ListEntry.COLUMN.LIST, savedList.mUUID);
        newEntryCV.put(ListEntry.COLUMN.PRODUCT, savedProduct.mUUID);
        if (_prioUsed) {
            newEntryCV.put(ListEntry.COLUMN.PRIORITY, _prio);
        }
        ListEntry rtn;
        if (entryCheck.getCount() == 0) {
            newEntryCV.put(ListEntry.COLUMN.AMOUNT, _amount);
            entryCheck.close();

            Uri newEntryUri = mResolver.insert(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                            savedList.getUriPath() + "/entry"),
                    newEntryCV);
            if (newEntryUri == null) {
                return null;
            }
            rtn = new ListEntry();
            rtn.mUUID = newEntryUri.getLastPathSegment();
            rtn.mAmount = _amount;
            rtn.mList = savedList;
            rtn.mProduct = savedProduct;
            rtn.mPriority = (_prioUsed ? _prio : ListEntry.DEFAULTS.PRIORITY);
            rtn.mStruck = (ListEntry.DEFAULTS.STRUCK != 0);

            mBus.post(new ListItemChangedMessage(Change.CREATED, rtn));
        } else {
            entryCheck.moveToFirst();
            if (_addAmount) {
                newEntryCV.put(ListEntry.COLUMN.AMOUNT, _amount + entryCheck.getFloat(
                        entryCheck.getColumnIndex(ListEntry.COLUMN.AMOUNT)));
            } else {
                newEntryCV.put(ListEntry.COLUMN.AMOUNT, _amount);
            }

            String entryUUID = entryCheck.getString(entryCheck.getColumnIndex(ListEntry.COLUMN.ID));
            int updatedItems = mResolver.update(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                            savedList.getUriPath() + "/entry/" + entryUUID),
                    newEntryCV,
                    null, null);
            rtn = getEntryById(entryUUID);
            if (updatedItems != 0) {
                mBus.post(new ListItemChangedMessage(Change.CHANGED, rtn));
            }
        }
        return rtn;
    }

    private ListEntry unstrikeItem(ListEntry _toChange, boolean _reload) {
        return updateStruckItem(_toChange, _reload, false);
    }

    private ListEntry updateStruckItem(ListEntry _toChange, boolean _reload, boolean _struck) {
        if (_toChange == null) {
            return null;
        }

        ContentValues entryUpdateCV = new ContentValues(1);
        entryUpdateCV.put(ListEntry.COLUMN.STRUCK, _struck);
        int chagedItems = mResolver.update(_toChange.toUri(InstalistProvider.BASE_CONTENT_URI),
                entryUpdateCV,
                null, null);
        ListEntry rtn;
        if (_reload) {
            rtn = getEntryById(_toChange.mUUID);
        } else {
            _toChange.mStruck = _struck;
            rtn = _toChange;
        }
        if (chagedItems > 0) {
            mBus.post(new ListItemChangedMessage(Change.CHANGED, rtn));
        }
        return rtn;
    }

    private ListEntry strikeItem(ListEntry _item, boolean _reload) {
        return updateStruckItem(_item, _reload, true);
    }

//endregion Private methods

}
