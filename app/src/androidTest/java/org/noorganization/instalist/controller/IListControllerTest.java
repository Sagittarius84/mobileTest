package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.provider.ProviderTestUtils;
import org.noorganization.instalist.provider.internal.ProductProvider;

import java.util.List;
import java.util.UUID;

public class IListControllerTest extends AndroidTestCase {

    private ShoppingList mListWork;
    private ShoppingList mListHome;
    private ListEntry mListEntryButterForWork;
    private Product mProductBread;
    private Product mProductButter;
    private Category mCategoryHardwareStore;
    private ContentResolver mResolver;

    private IListController mListController;

    public void setUp() throws Exception {
        super.setUp();

        mResolver = mContext.getContentResolver();
        tearDown();
        mListController = ControllerFactory.getListController(mContext);

        ContentValues listHomeCV = new ContentValues(1);
        listHomeCV.put(ShoppingList.COLUMN.NAME, "_TEST_home");
        Uri listHomeUri = mResolver.insert(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "category/-/list"), listHomeCV);
        assertNotNull(listHomeUri);
        mListHome = new ShoppingList(listHomeUri.getLastPathSegment(), "_TEST_home", null);

        ContentValues listWorkCV = new ContentValues(1);
        listWorkCV.put(ShoppingList.COLUMN.NAME, "_TEST_work");
        Uri listWorkUri = mResolver.insert(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "category/-/list"), listWorkCV);
        assertNotNull(listWorkUri);
        mListHome = new ShoppingList(listWorkUri.getLastPathSegment(), "_TEST_work", null);

        ContentValues prodBreadCV = new ContentValues(1);
        prodBreadCV.put(Product.COLUMN.NAME, "_TEST_bread");
        Uri prodBreadUri = mResolver.insert(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "product"), prodBreadCV);
        assertNotNull(prodBreadUri);
        mProductBread = new Product(prodBreadUri.getLastPathSegment(), "_TEST_bread", null,
                Product.DEFAULTS.DEFAULT_AMOUNT, Product.DEFAULTS.STEP_AMOUNT);

        ContentValues prodButterCV = new ContentValues(1);
        prodButterCV.put(Product.COLUMN.NAME, "_TEST_butter");
        Uri prodButterUri = mResolver.insert(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "product"), prodButterCV);
        assertNotNull(prodButterUri);
        mProductBread = new Product(prodButterUri.getLastPathSegment(), "_TEST_butter", null,
                Product.DEFAULTS.DEFAULT_AMOUNT, Product.DEFAULTS.STEP_AMOUNT);

        ContentValues catHardwareStoreCV = new ContentValues(1);
        catHardwareStoreCV.put(Category.COLUMN.NAME, "_TEST_hardware store");
        Uri catHardwareStoreUri = mResolver.insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                catHardwareStoreCV);
        assertNotNull(catHardwareStoreUri);
        mCategoryHardwareStore = new Category(catHardwareStoreUri.getLastPathSegment(),
                "_TEST_hardware store");

        ContentValues entryButter4WorkCV = new ContentValues(3);
        entryButter4WorkCV.put(ListEntry.COLUMN.PRODUCT, mProductButter.mUUID);
        entryButter4WorkCV.put(ListEntry.COLUMN.LIST, mListWork.mUUID);
        entryButter4WorkCV.put(ListEntry.COLUMN.AMOUNT, 2.0f);
        Uri entryButter4WorkUri = mResolver.insert(Uri.withAppendedPath(
                InstalistProvider.BASE_CONTENT_URI, mListWork.getUriPath() + "/entry"),
                entryButter4WorkCV);
        assertNotNull(entryButter4WorkUri);
        mListEntryButterForWork = new ListEntry(entryButter4WorkUri.getLastPathSegment(), mListWork,
                mProductButter, 2.0f, (ListEntry.DEFAULTS.STRUCK != 0), ListEntry.DEFAULTS.PRIORITY);
    }

    public void tearDown() throws Exception {
        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                Category.COLUMN.NAME + " LIKE '_TEST_%'", null);
        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list"),
                Category.COLUMN.NAME + " LIKE '_TEST_%'", null);
        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "product"),
                Category.COLUMN.NAME + " LIKE '_TEST_%'", null);
    }

    private int getListCount() {
        Cursor lists = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "list"), null, null, null, null);
        assertNotNull(lists);
        int rtn = lists.getCount();
        lists.close();
        return rtn;
    }

    private int getEntryCount() {
        Cursor entries = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "entry"), null, null, null, null);
        assertNotNull(entries);
        int rtn = entries.getCount();
        entries.close();
        return rtn;
    }

    public void testCreateShoppingList() {
        int previousCount = getListCount();
        // negative test
        assertNull(mListController.addList(null));
        assertNull(mListController.addList(""));
        assertNull(mListController.addList("_TEST_work"));
        assertNull(mListController.addList(null, mCategoryHardwareStore));
        assertEquals(previousCount, getListCount());

        // positive test: w/o category
        ShoppingList createdListWOCat = mListController.addList("_TEST_list");
        assertNotNull(createdListWOCat);
        assertNotNull(createdListWOCat.mUUID);
        assertEquals("_TEST_list", createdListWOCat.mName);
        assertNull(createdListWOCat.mCategory);

        ShoppingList savedListWOCat = mListController.getListById(createdListWOCat.mUUID);
        assertEquals(createdListWOCat, savedListWOCat);
        assertEquals(previousCount + 1, getListCount());

        // positive test: w category
        ShoppingList createdListWCat = mListController.addList("_TEST_list2", mCategoryHardwareStore);
        assertNotNull(createdListWCat);
        assertNotNull(createdListWCat.mUUID);
        assertEquals("_TEST_list2", createdListWCat.mName);
        assertEquals(mCategoryHardwareStore, createdListWCat.mCategory);

        ShoppingList savedListWCat = mListController.getListById(createdListWCat.mUUID);
        assertEquals(createdListWCat, savedListWCat);
        assertEquals(previousCount + 2, getListCount());
    }

    public void testFindShoppingListById() {
        // negative test
        assertNull(mListController.getListById(""));
        assertNull(mListController.getListById(UUID.randomUUID().toString()));

        // positive test
        ShoppingList foundList = mListController.getListById(mListWork.mUUID);
        assertEquals(mListWork, foundList);
    }

    public void testDeleteShoppingList() {
        int previousCount = getListCount();
        // negative test
        assertFalse(mListController.removeList(null));
        assertFalse(mListController.removeList(new ShoppingList()));
        assertEquals(previousCount, getListCount());

        // positive test
        assertTrue(mListController.removeList(mListWork));
        assertNull(mListController.getListById(mListWork.mUUID));
        assertEquals(previousCount - 1, getListCount());
        assertNotNull(mListController.getListById(mListHome.mUUID));
    }

    public void testAddOrChangeItem() throws Exception {
        int previousCount = getEntryCount();
        // negative test: wrong inputs or not existing items
        assertNull(mListController.addOrChangeItem(null, mProductButter, 1.0f));
        assertNull(mListController.addOrChangeItem(mListHome, null, 1.0f));
        assertNull(mListController.addOrChangeItem(mListHome, mProductButter, -1.0f));
        assertEquals(previousCount, getEntryCount());

        // negative test: wrong inputs for existing item
        ListEntry returnedUnchangedEntry = mListController.addOrChangeItem(mListWork, mProductButter, -1.0f);
        assertNotNull(returnedUnchangedEntry);
        ListEntry unchangedEntry = mListController.getEntryById(mListEntryButterForWork.mUUID);
        assertEquals(mListEntryButterForWork, unchangedEntry);
        assertEquals(returnedUnchangedEntry, unchangedEntry);

        // positive test: change existing product
        ListEntry returnedEntry = mListController.addOrChangeItem(mListWork, mProductButter, 7.0f, 0);
        assertNotNull(returnedEntry);
        ListEntry changedEntry = mListController.getEntryById(mListEntryButterForWork.mUUID);
        assertNotNull(changedEntry);
        assertEquals(7.0f, changedEntry.mAmount, 0.001f);
        assertFalse(changedEntry.mStruck);
        assertEquals(0, returnedEntry.mPriority);
        assertEquals(mListWork, changedEntry.mList);
        assertEquals(mProductButter, changedEntry.mProduct);
        assertEquals(changedEntry, returnedEntry);

        // positive test: create entry
        ListEntry returnedEntry2 = mListController.addOrChangeItem(mListHome, mProductButter, 2.0f, 5);
        assertNotNull(returnedEntry2);
        assertEquals(mListHome, returnedEntry2.mList);
        assertEquals(mProductButter, returnedEntry2.mProduct);
        assertEquals(2.0f, returnedEntry2.mAmount, 0.001f);
        assertFalse(returnedEntry2.mStruck);
        assertEquals(5, returnedEntry2.mPriority);
        ListEntry createdEntry2 = mListController.getEntryById(returnedEntry2.mUUID);
        assertEquals(createdEntry2, returnedEntry2);
        assertEquals(previousCount + 1, getEntryCount());

        // positive test create second entry
        ListEntry returnedSecondEntry = mListController.addOrChangeItem(mListHome, mProductBread, 1.0f);
        assertNotNull(returnedSecondEntry);
        assertEquals(mListHome, returnedSecondEntry.mList);
        assertEquals(mProductBread, returnedSecondEntry.mProduct);
        assertEquals(1.0f, returnedSecondEntry.mAmount, 0.001f);
        assertFalse(returnedSecondEntry.mStruck);
        assertEquals(0, returnedSecondEntry.mPriority);
        ListEntry createdSecondEntry = mListController.getEntryById(returnedSecondEntry.mUUID);
        assertEquals(returnedSecondEntry, createdSecondEntry);
        assertEquals(previousCount + 2, getEntryCount());

        // positive test: update second new entry
        ListEntry returnedThirdEntry = mListController.addOrChangeItem(mListHome, mProductBread, 1.0f, true);
        assertNotNull(returnedThirdEntry);
        assertEquals(2.0f, returnedThirdEntry.mAmount, 0.001f);
        assertEquals(returnedThirdEntry, mListController.getEntryById(returnedSecondEntry.mUUID));
    }

    public void testStrikeAllItems() throws Exception {
        // negative test
        mListController.strikeAllItems(null);
        mListController.strikeAllItems(new ShoppingList());
        assertEquals(mListEntryButterForWork,
                mListController.getEntryById(mListEntryButterForWork.mUUID));

        ContentValues breadForWorkCV = new ContentValues(3);
        breadForWorkCV.put(ListEntry.COLUMN.LIST, mListWork.mUUID);
        breadForWorkCV.put(ListEntry.COLUMN.PRODUCT, mProductBread.mUUID);
        breadForWorkCV.put(ListEntry.COLUMN.STRUCK, false);
        Uri breadForWorkUri = mResolver.insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        mListWork.getUriPath() + "/entry"), breadForWorkCV);
        assertNotNull(breadForWorkUri);
        ListEntry entryBreadForWork = mListController.getEntryById(breadForWorkUri.
                getLastPathSegment());

        // positive test
        mListController.strikeAllItems(mListWork);
        assertTrue(mListController.getEntryById(mListEntryButterForWork.mUUID).mStruck);
        assertTrue(mListController.getEntryById(entryBreadForWork.mUUID).mStruck);
    }

    public void testUnstrikeAllItems() throws Exception {
        // negative test
        mListController.unstrikeAllItems(null);
        mListController.unstrikeAllItems(new ShoppingList());

        ContentValues breadForWorkCV = new ContentValues(3);
        breadForWorkCV.put(ListEntry.COLUMN.LIST, mListWork.mUUID);
        breadForWorkCV.put(ListEntry.COLUMN.PRODUCT, mProductBread.mUUID);
        breadForWorkCV.put(ListEntry.COLUMN.STRUCK, true);
        Uri breadForWorkUri = mResolver.insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        mListWork.getUriPath() + "/entry"), breadForWorkCV);
        assertNotNull(breadForWorkUri);
        ListEntry entryBreadForWork = mListController.getEntryById(breadForWorkUri.
                getLastPathSegment());

        // positive test
        mListController.unstrikeAllItems(mListWork);
        assertFalse(mListController.getEntryById(mListEntryButterForWork.mUUID).mStruck);
        assertFalse(mListController.getEntryById(entryBreadForWork.mUUID).mStruck);
    }

    public void testStrikeItem() throws Exception {
        insertTestData();

        assertNull(mListController.strikeItem(mListHome, mProductBread));

        ListEntry changedFirstListEntry = mListController.strikeItem(mListWork, mProductButter);
        assertNotNull(changedFirstListEntry);
        assertEquals(true, changedFirstListEntry.mStruck);

        ListEntry changedSecondListEntry = mListController.strikeItem(mListEntryButterForWork);
        assertNotNull(changedSecondListEntry);
        assertEquals(changedFirstListEntry, changedSecondListEntry);
    }

    public void testUnstrikeItem() throws Exception {
        insertTestData();

        mListEntryButterForWork = mListController.strikeItem(mListEntryButterForWork);

        assertNotNull(mListEntryButterForWork);
        assertNull(mListController.unstrikeItem(mListHome, mProductBread));

        ListEntry changedFirstListEntry = mListController.unstrikeItem(mListWork, mProductButter);
        assertNotNull(changedFirstListEntry);
        assertFalse(changedFirstListEntry.mStruck);

        ListEntry changedSecondListEntry = mListController.unstrikeItem(mListEntryButterForWork);
        assertNotNull(changedSecondListEntry);
        assertEquals(changedFirstListEntry, changedSecondListEntry);

    }

    public void testRemoveItem() throws Exception {
        insertTestData();
        assertFalse(mListController.removeItem(mListHome, mProductBread));

        assertTrue(mListController.removeItem(mListWork, mProductButter));
        assertNull(mListController.getEntryById(mListEntryButterForWork.mUUID));
    }

    public void testAddList() throws Exception {
        insertTestData();
        assertNull(mListController.addList("_TEST_work"));

        ShoppingList returnedList = mListController.addList("_TEST_forMom");
        assertEquals("_TEST_forMom", returnedList.mName);
        assertNull(returnedList.mCategory);
        ShoppingList savedList = mListController.getListById(returnedList.mUUID);
        assertNotNull(savedList);
        assertEquals(returnedList, savedList);

        Category notSavedCat = new Category();
        notSavedCat.mName = "_TEST_not saved";

        assertNull(mListController.addList("_TEST_hardware", notSavedCat));
        assertNull(mListController.addList(null, mCategoryHardwareStore));

        ShoppingList returned2ndList = mListController.addList("_TEST_hardware", mCategoryHardwareStore);
        assertNotNull(returned2ndList);
        assertEquals(mCategoryHardwareStore, returned2ndList.mCategory);
    }

    public void testRemoveList() throws Exception {
        insertTestData();

        // list is filled, so should not be deletable
        assertFalse(mListController.removeList(mListWork));
        assertEquals(mListWork, mListController.getListById(mListWork.mUUID));

        assertTrue(mListController.removeList(mListHome));
        assertNull(mListController.getEntryById(mListHome.mUUID));
    }

    public void testRenameList() throws Exception {
        insertTestData();

        ShoppingList changedList1 = mListController.renameList(mListWork, "_TEST_home");
        assertEquals("_TEST_work", changedList1.mName);
        assertEquals(changedList1, mListController.getListById(mListWork.mUUID));

        ShoppingList changedList2 = mListController.renameList(mListWork, "_TEST_forMom");
        assertEquals("_TEST_forMom", changedList2.mName);
        assertEquals(changedList2, mListController.getListById(mListWork.mUUID));
    }

    public void testMoveToCategory() throws Exception {
        insertTestData();

        Category notSavedCat = new Category();
        notSavedCat.mName = "_TEST_not saved";
        assertEquals(mListWork, mListController.moveToCategory(mListWork, notSavedCat));
        assertNull(mListController.moveToCategory(null, mCategoryHardwareStore));

        ShoppingList returnedList1 = mListController.moveToCategory(mListWork, mCategoryHardwareStore);
        assertNotNull(returnedList1);
        assertEquals(mListWork.mName, returnedList1.mName);
        assertEquals(mCategoryHardwareStore, returnedList1.mCategory);
        assertEquals(returnedList1, mListController.getListById(mListWork.mUUID));
    }

    public void testSetItemPriority() throws Exception {
        insertTestData();

        assertNull(mListController.setItemPriority(null, 5));
        ListEntry notSavedEntry = new ListEntry(mListHome, mProductBread, 1.0f);
        assertNull(mListController.setItemPriority(notSavedEntry, 5));

        ListEntry returnedEntry1 = mListController.setItemPriority(mListEntryButterForWork, 5);
        assertNotNull(returnedEntry1);
        assertEquals(5, returnedEntry1.mPriority);
        assertEquals(mListEntryButterForWork.mAmount, returnedEntry1.mAmount);
        assertEquals(mListEntryButterForWork.mList, returnedEntry1.mList);
        assertEquals(mListEntryButterForWork.mProduct, returnedEntry1.mProduct);
        assertEquals(mListEntryButterForWork.mStruck, returnedEntry1.mStruck);
        assertEquals(returnedEntry1, mListController.getEntryById(mListEntryButterForWork.mUUID));
    }
}