package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.orm.SugarRecord;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.provider.internal.CategoryProvider;
import org.noorganization.instalist.provider.internal.ShoppingListProvider;

import java.util.List;

public class IListControllerTest extends AndroidTestCase {

    private ShoppingList mListWork;
    private ShoppingList mListHome;
    private ListEntry mListEntryButterForWork;
    private Product mProductBread;
    private Product mProductButter;
    private Category mCategoryHardwareStore;
    private ContentResolver mResolver;

    private IListController mLML4Test;

    public void setUp() throws Exception {
        super.setUp();

        ContentValues newListCV = new ContentValues(2);
        String insertUri = InstalistProvider.BASE_CONTENT_URI.getPath().concat("category/").concat("?").concat("/list");

        mResolver = mContext.getContentResolver();
        Cursor categoryCursor = mResolver.query(Uri.parse(InstalistProvider.BASE_CONTENT_URI.getPath().concat("category")),
                Category.COLUMN.ALL_COLUMNS,
                Category.COLUMN.ID + "=?",
                new String[]{"-"},
                null
                );

        assertNotNull(categoryCursor);
        assertTrue(categoryCursor.getCount() > 0);

        Category category = new Category();
        category.mUUID = "-";
        category.mName = categoryCursor.getString(categoryCursor.getColumnIndex(Category.COLUMN.NAME));

        Uri uri = Uri.parse(String.format(insertUri, "-"));
        mListWork = new ShoppingList();
        mListWork.mName = "_Test_work";
        mListWork.mCategory = category;

        Uri insertedListUri = mResolver.insert(uri, mListWork.toContentValues());
        assertNotNull(insertedListUri);

        mListWork.mUUID = insertedListUri.getLastPathSegment();


        uri = Uri.parse(String.format(insertUri, "-"));
        mListHome = new ShoppingList();
        mListHome.mName = "_TEST_home";
        mListHome.mCategory = category;

        insertedListUri = mResolver.insert(uri, mListHome.toContentValues());
        assertNotNull(insertedListUri);

        mListHome.mUUID = insertedListUri.getLastPathSegment();

        // insert products
        mProductBread = new Product("_TEST_bread", null);
        mProductBread.save();
        mProductButter = new Product("_TEST_butter", null);
        mProductButter.save();
        mListEntryButterForWork = new ListEntry(mListWork, mProductButter, 1.0f);
        mListEntryButterForWork.save();
        mCategoryHardwareStore = new Category("_TEST_hardware store");
        mCategoryHardwareStore.save();

        mLML4Test = ControllerFactory.getListController();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(ListEntry.class,
                "m_list = ? or m_list = ? or m_product = ? or m_product = ?",
                mListWork.getId() + "",
                mListHome.getId() + "",
                mProductBread.getId() + "",
                mProductButter.getId() + "");

        SugarRecord.deleteAll(ShoppingList.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Product.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Category.class, "m_name LIKE '_TEST_%'");
    }

    public void testCreateShoppingList(){
        // TODO implement
    }

    public void testDeleteShoppingList(){
        // TODO implement
    }

    public void testAddOrChangeItem() throws Exception {
        assertNull(mLML4Test.addOrChangeItem(null, mProductButter, 1.0f));
        assertNull(mLML4Test.addOrChangeItem(mListHome, null, 1.0f));
        assertNull(mLML4Test.addOrChangeItem(mListHome, mProductButter, -1.0f));

        ListEntry returnedUnchangedEntry = mLML4Test.addOrChangeItem(mListWork, mProductButter, -1.0f);
        assertNotNull(returnedUnchangedEntry);
        ListEntry unchangedEntry = SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId());
        assertEquals(mListEntryButterForWork, unchangedEntry);
        assertEquals(returnedUnchangedEntry, unchangedEntry);

        ListEntry returnedEntry = mLML4Test.addOrChangeItem(mListWork, mProductButter, 2.0f, 0);
        assertNotNull(returnedEntry);
        ListEntry changedEntry = SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId());
        assertNotNull(changedEntry);
        assertNotNull(changedEntry.mProduct);
        assertNotNull(changedEntry.mList);
        assertEquals(2.0f, changedEntry.mAmount, 0.001f);
        assertFalse(changedEntry.mStruck);
        assertEquals(0, returnedEntry.mPriority);
        assertEquals(mListWork.getId(), changedEntry.mList.getId());
        assertEquals(mProductButter.getId(), changedEntry.mProduct.getId());
        assertEquals(changedEntry, returnedEntry);

        ListEntry returnedEntry2 = mLML4Test.addOrChangeItem(mListHome, mProductButter, 2.0f, 5);
        assertNotNull(returnedEntry2);
        assertEquals(mListHome, returnedEntry2.mList);
        assertEquals(mProductButter, returnedEntry2.mProduct);
        assertEquals(2.0f, returnedEntry2.mAmount, 0.001f);
        assertFalse(returnedEntry2.mStruck);
        assertEquals(5, returnedEntry2.mPriority);
        ListEntry createdEntry2 = SugarRecord.findById(ListEntry.class, returnedEntry2.getId());
        assertEquals(createdEntry2, returnedEntry2);

        ListEntry returnedSecondEntry = mLML4Test.addOrChangeItem(mListHome, mProductBread, 1.0f);
        assertNotNull(returnedSecondEntry);
        assertEquals(mListHome, returnedSecondEntry.mList);
        assertEquals(mProductBread, returnedSecondEntry.mProduct);
        assertEquals(1.0f, returnedSecondEntry.mAmount, 0.001f);
        assertFalse(returnedSecondEntry.mStruck);
        assertEquals(0, returnedSecondEntry.mPriority);
        ListEntry createdSecondEntry = SugarRecord.findById(ListEntry.class, returnedSecondEntry.getId());
        assertEquals(returnedSecondEntry, createdSecondEntry);

        List<ListEntry> allEntriesOfHomeList = mListHome.getEntries();
        assertEquals(2, allEntriesOfHomeList.size());
        assertTrue(allEntriesOfHomeList.contains(returnedEntry2));
        assertTrue(allEntriesOfHomeList.contains(returnedSecondEntry));

        ListEntry returnedThirdEntry = mLML4Test.addOrChangeItem(mListHome, mProductBread, 1.0f, true);
        assertNotNull(returnedThirdEntry);
        assertEquals(2.0f, returnedThirdEntry.mAmount, 0.001f);
        assertEquals(returnedThirdEntry, SugarRecord.findById(ListEntry.class, returnedSecondEntry.getId()));
    }

    public void testStrikeAllItems() throws Exception {
        ListEntry breadForWork = new ListEntry(mListWork, mProductBread, 1.0f, false);
        breadForWork.save();

        mLML4Test.strikeAllItems(mListWork);

        assertTrue(SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId()).mStruck);
        assertTrue(SugarRecord.findById(ListEntry.class, breadForWork.getId()).mStruck);
    }

    public void testUnstrikeAllItems() throws Exception {

        ListEntry breadForWork = new ListEntry(mListWork, mProductBread, 1.0f, true);
        breadForWork.save();

        mListEntryButterForWork.mStruck = true;
        mListEntryButterForWork.save();

        mLML4Test.unstrikeAllItems(mListWork);

        assertFalse(SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId()).mStruck);
        assertFalse(SugarRecord.findById(ListEntry.class, breadForWork.getId()).mStruck);
    }

    public void testStrikeItem() throws Exception {
        assertNull(mLML4Test.strikeItem(mListHome, mProductBread));

        ListEntry changedFirstListEntry = mLML4Test.strikeItem(mListWork, mProductButter);
        assertNotNull(changedFirstListEntry);
        assertEquals(true, changedFirstListEntry.mStruck);

        ListEntry changedSecondListEntry = mLML4Test.strikeItem(mListEntryButterForWork);
        assertNotNull(changedSecondListEntry);
        assertEquals(changedFirstListEntry, changedSecondListEntry);
    }

    public void testUnstrikeItem() throws Exception {

        mListEntryButterForWork.mStruck = true;
        mListEntryButterForWork.save();

        assertNull(mLML4Test.unstrikeItem(mListHome, mProductBread));

        ListEntry changedFirstListEntry = mLML4Test.unstrikeItem(mListWork, mProductButter);
        assertNotNull(changedFirstListEntry);
        assertFalse(changedFirstListEntry.mStruck);

        ListEntry changedSecondListEntry = mLML4Test.unstrikeItem(mListEntryButterForWork);
        assertNotNull(changedSecondListEntry);
        assertEquals(changedFirstListEntry, changedSecondListEntry);

    }

    public void testRemoveItem() throws Exception {
        assertFalse(mLML4Test.removeItem(mListHome, mProductBread));

        assertTrue(mLML4Test.removeItem(mListWork, mProductButter));
        assertNull(SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId()));
    }

    public void testAddList() throws Exception {
        assertNull(mLML4Test.addList("_TEST_work"));

        ShoppingList returnedList = mLML4Test.addList("_TEST_forMom");
        assertEquals("_TEST_forMom", returnedList.mName);
        assertNull(returnedList.mCategory);
        ShoppingList savedList = SugarRecord.findById(ShoppingList.class, returnedList.getId());
        assertNotNull(savedList);
        assertEquals(returnedList, savedList);

        Category notSavedCat = new Category("_TEST_not saved");
        assertNull(mLML4Test.addList("_TEST_hardware", notSavedCat));
        assertNull(mLML4Test.addList(null, mCategoryHardwareStore));

        ShoppingList returned2ndList = mLML4Test.addList("_TEST_hardware", mCategoryHardwareStore);
        assertNotNull(returned2ndList);
        assertEquals(mCategoryHardwareStore, returned2ndList.mCategory);
    }

    public void testRemoveList() throws Exception {
        // list is filled, so should not be deletable
        assertFalse(mLML4Test.removeList(mListWork));
        assertEquals(mListWork, SugarRecord.findById(ShoppingList.class, mListWork.getId()));

        assertTrue(mLML4Test.removeList(mListHome));
        assertNull(SugarRecord.findById(ShoppingList.class, mListHome.getId()));
    }

    public void testRenameList() throws Exception {
        ShoppingList changedList1 = mLML4Test.renameList(mListWork, "_TEST_home");
        assertEquals("_TEST_work", changedList1.mName);
        assertEquals(changedList1, SugarRecord.findById(ShoppingList.class, mListWork.getId()));

        ShoppingList changedList2 = mLML4Test.renameList(mListWork, "_TEST_forMom");
        assertEquals("_TEST_forMom", changedList2.mName);
        assertEquals(changedList2, SugarRecord.findById(ShoppingList.class, mListWork.getId()));
    }

    public void testMoveToCategory() throws Exception {
        Category notSavedCat = new Category("_TEST_not saved");
        assertEquals(mListWork, mLML4Test.moveToCategory(mListWork, notSavedCat));
        assertNull(mLML4Test.moveToCategory(null, mCategoryHardwareStore));

        ShoppingList returnedList1 = mLML4Test.moveToCategory(mListWork, mCategoryHardwareStore);
        assertNotNull(returnedList1);
        assertEquals(mListWork.mName, returnedList1.mName);
        assertEquals(mCategoryHardwareStore, returnedList1.mCategory);
        assertEquals(returnedList1, SugarRecord.findById(ShoppingList.class, mListWork.getId()));
    }

    public void testSetItemPriority() throws Exception {
        assertNull(mLML4Test.setItemPriority(null, 5));
        ListEntry notSavedEntry = new ListEntry(mListHome, mProductBread, 1.0f);
        assertNull(mLML4Test.setItemPriority(notSavedEntry, 5));

        ListEntry returnedEntry1 = mLML4Test.setItemPriority(mListEntryButterForWork, 5);
        assertNotNull(returnedEntry1);
        assertEquals(5, returnedEntry1.mPriority);
        assertEquals(mListEntryButterForWork.mAmount, returnedEntry1.mAmount);
        assertEquals(mListEntryButterForWork.mList, returnedEntry1.mList);
        assertEquals(mListEntryButterForWork.mProduct, returnedEntry1.mProduct);
        assertEquals(mListEntryButterForWork.mStruck, returnedEntry1.mStruck);
        assertEquals(returnedEntry1, SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId()));
    }
}