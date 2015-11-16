package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;

import java.util.List;

public class IListControllerTest extends AndroidTestCase {

    private ShoppingList mListWork;
    private ShoppingList mListHome;
    private ListEntry mListEntryButterForWork;
    private Product mProductBread;
    private Product mProductButter;
    private Category mCategoryHardwareStore;
    private ContentResolver mResolver;

    private IListController mListController;
    private ICategoryController mCategoryController;
    private IProductController mProductController;

    public void setUp() throws Exception {
        super.setUp();

        mResolver = mContext.getContentResolver();
        mListController = ControllerFactory.getListController(mContext);
        mCategoryController = ControllerFactory.getCategoryController(mContext);
        mProductController = ControllerFactory.getProductController(mContext);
    }

    public void insertTestData() {
        createLists();
        // insert products
        mProductBread = mProductController.createProduct("_TEST_bread", null, 1.0f, 1.0f);
        assertNotNull(mProductBread);

        mProductButter = mProductController.createProduct("_TEST_butter", null, 1.0f, 1.0f);
        assertNotNull(mProductButter);

        mListEntryButterForWork = mListController.addOrChangeItem(mListWork, mProductButter, 1.0f);

        mCategoryHardwareStore = mCategoryController.createCategory("_TEST_hardware store");
        assertNotNull(mCategoryHardwareStore);

    }

    public void tearDown() throws Exception {

        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"), null, null);
        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"), null, null);
        mResolver.delete(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), null, null);
        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"), null, null);
    }

    public void testCreateShoppingList() {
        ShoppingList shoppingList = mListController.addList("test_list");
        assertNotNull(shoppingList);
        Cursor shoppingListCursor = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"),
                ShoppingList.COLUMN.ALL_COLUMNS,
                ShoppingList.COLUMN.ID + "=?",
                new String[]{shoppingList.mUUID},
                null
        );

        assertNotNull(shoppingListCursor);
        assertEquals(1, shoppingListCursor.getCount());

        assertTrue(shoppingListCursor.moveToFirst());
        assertEquals(shoppingList.mUUID, shoppingListCursor.getString(shoppingListCursor.getColumnIndex(ShoppingList.COLUMN.ID)));
        assertEquals(shoppingList.mName, shoppingListCursor.getString(shoppingListCursor.getColumnIndex(ShoppingList.COLUMN.NAME)));
        assertEquals(shoppingList.mCategory.mUUID, shoppingListCursor.getString(shoppingListCursor.getColumnIndex(ShoppingList.COLUMN.CATEGORY)));

        shoppingListCursor.close();
    }

    public void testFindShoppingListById() {
        ShoppingList shoppingList1 = mListController.addList("test_list1");
        ShoppingList shoppingList2 = mListController.addList("test_list2");

        assertNotNull(shoppingList1);
        assertNotNull(shoppingList2);

        ShoppingList shoppingListTest1 = mListController.getListById(shoppingList1.mUUID);
        assertEquals(shoppingList1.mUUID, shoppingListTest1.mUUID);
        assertEquals(shoppingList1.mName, shoppingListTest1.mName);
        assertEquals(shoppingList1.mCategory.mUUID, shoppingListTest1.mCategory.mUUID);

    }

    public void testDeleteShoppingList() {
        ShoppingList shoppingList = mListController.addList("test_list");
        ShoppingList shoppingList2 = mListController.addList("test_list2");

        assertNotNull(shoppingList);
        assertTrue(mListController.removeList(shoppingList));

        Cursor shoppingListCursor = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"),
                ShoppingList.COLUMN.ALL_COLUMNS,
                ShoppingList.COLUMN.ID + "=?",
                new String[]{shoppingList.mUUID},
                null
        );

        assertNotNull(shoppingListCursor);
        assertEquals(0, shoppingListCursor.getCount());
    }

    public void testAddOrChangeItem() throws Exception {
        createLists();

        assertNull(mListController.addOrChangeItem(null, mProductButter, 1.0f));
        assertNull(mListController.addOrChangeItem(mListHome, null, 1.0f));
        assertNull(mListController.addOrChangeItem(mListHome, mProductButter, -1.0f));

        ListEntry returnedUnchangedEntry = mListController.addOrChangeItem(mListWork, mProductButter, -1.0f);
        assertNotNull(returnedUnchangedEntry);
        ListEntry unchangedEntry = mListController.getEntryById(mListEntryButterForWork.mUUID);

        assertEquals(mListEntryButterForWork, unchangedEntry);
        assertEquals(returnedUnchangedEntry, unchangedEntry);

        ListEntry returnedEntry = mListController.addOrChangeItem(mListWork, mProductButter, 2.0f, 0);
        assertNotNull(returnedEntry);
        ListEntry changedEntry = mListController.getEntryById(mListEntryButterForWork.mUUID);
        assertNotNull(changedEntry);
        assertNotNull(changedEntry.mProduct);
        assertNotNull(changedEntry.mList);
        assertEquals(2.0f, changedEntry.mAmount, 0.001f);
        assertFalse(changedEntry.mStruck);
        assertEquals(0, returnedEntry.mPriority);
        assertEquals(mListWork.mUUID, changedEntry.mList.mUUID);
        assertEquals(mProductButter.mUUID, changedEntry.mProduct.mUUID);
        assertEquals(changedEntry, returnedEntry);

        ListEntry returnedEntry2 = mListController.addOrChangeItem(mListHome, mProductButter, 2.0f, 5);
        assertNotNull(returnedEntry2);
        assertEquals(mListHome, returnedEntry2.mList);
        assertEquals(mProductButter, returnedEntry2.mProduct);
        assertEquals(2.0f, returnedEntry2.mAmount, 0.001f);
        assertFalse(returnedEntry2.mStruck);
        assertEquals(5, returnedEntry2.mPriority);
        ListEntry createdEntry2 = mListController.getEntryById(returnedEntry2.mUUID);
        assertEquals(createdEntry2, returnedEntry2);

        ListEntry returnedSecondEntry = mListController.addOrChangeItem(mListHome, mProductBread, 1.0f);
        assertNotNull(returnedSecondEntry);
        assertEquals(mListHome, returnedSecondEntry.mList);
        assertEquals(mProductBread, returnedSecondEntry.mProduct);
        assertEquals(1.0f, returnedSecondEntry.mAmount, 0.001f);
        assertFalse(returnedSecondEntry.mStruck);
        assertEquals(0, returnedSecondEntry.mPriority);
        ListEntry createdSecondEntry = mListController.getEntryById(returnedSecondEntry.mUUID);
        assertEquals(returnedSecondEntry, createdSecondEntry);

        List<ListEntry> allEntriesOfHomeList = mListController.listAllListEntries(mListHome.mUUID, mListHome.mCategory.mUUID);
        assertEquals(2, allEntriesOfHomeList.size());
        assertTrue(allEntriesOfHomeList.contains(returnedEntry2));
        assertTrue(allEntriesOfHomeList.contains(returnedSecondEntry));

        ListEntry returnedThirdEntry = mListController.addOrChangeItem(mListHome, mProductBread, 1.0f, true);
        assertNotNull(returnedThirdEntry);
        assertEquals(2.0f, returnedThirdEntry.mAmount, 0.001f);
        assertEquals(returnedThirdEntry, mListController.getEntryById(returnedSecondEntry.mUUID));
    }

    private void createLists() {

        mListWork = mListController.addList("_Test_work");
        mListHome = mListController.addList("TEST_home");

        assertNotNull(mListWork);
        assertNotNull(mListHome);
    }

    public void testStrikeAllItems() throws Exception {
        ListEntry breadForWork = mListController.addOrChangeItem(mListWork, mProductBread, 1.0f, false);

        assertNotNull(breadForWork);

        mListController.strikeAllItems(mListWork);

        assertTrue(mListController.getEntryById(mListEntryButterForWork.mUUID).mStruck);
        assertTrue(mListController.getEntryById(breadForWork.mUUID).mStruck);
    }

    public void testUnstrikeAllItems() throws Exception {
        insertTestData();

        ListEntry breadForWork = mListController.addOrChangeItem(mListWork, mProductBread, 1.0f, true);

        assertNotNull(breadForWork);

        mListController.strikeItem(breadForWork);

        mListController.unstrikeAllItems(mListWork);

        assertFalse(mListController.getEntryById(mListEntryButterForWork.mUUID).mStruck);
        assertFalse(mListController.getEntryById(breadForWork.mUUID).mStruck);
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