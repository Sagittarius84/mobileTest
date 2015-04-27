package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

import org.noorganization.instalist.controller.implementation.ListController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;

public class ListModificationListenerTest extends AndroidTestCase {

    private ShoppingList mListWork;
    private ShoppingList mListHome;
    private ListEntry mListEntryButterForWork;
    private Product mProductBread;
    private Product mProductButter;

    private ListModificationListener mLML4Test;

    public void setUp() throws Exception {
        super.setUp();

        mListWork = new ShoppingList("_TEST_work");
        mListWork.save();
        mListHome = new ShoppingList("_TEST_home");
        mListHome.save();
        mProductBread = new Product("_TEST_bread", null);
        mProductBread.save();
        mProductButter = new Product("_TEST_butter", null);
        mProductButter.save();
        mListEntryButterForWork = new ListEntry(mListWork, mProductButter, 1.0f);
        mListEntryButterForWork.save();

        mLML4Test = ListController.getInstance();
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
    }

    public void testAddOrChangeItem() throws Exception {
        assertNull(mLML4Test.addOrChangeItem(null, mProductButter, 1.0f));
        assertNull(mLML4Test.addOrChangeItem(mListHome, null, 1.0f));
        assertNull(mLML4Test.addOrChangeItem(mListHome, mProductButter, -1.0f));

        assertNull(mLML4Test.addOrChangeItem(mListWork, mProductButter, -1.0f));
        ListEntry unchangedEntry = SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId());
        assertEquals(unchangedEntry, mListEntryButterForWork);

        ListEntry returnedEntry = mLML4Test.addOrChangeItem(mListWork, mProductButter, 2.0f);
        ListEntry changedEntry = SugarRecord.findById(ListEntry.class, mListEntryButterForWork.getId());
        assertEquals(2.0f, changedEntry.mAmount, 0.001f);
        assertFalse(changedEntry.mStruck);
        assertEquals(mListWork.getId(), changedEntry.mList.getId());
        assertEquals(mProductButter.getId(), changedEntry.mProduct.getId());
        assertEquals(changedEntry, returnedEntry);

        ListEntry returnedEntry2 = mLML4Test.addOrChangeItem(mListHome, mProductButter, 2.0f);
        ListEntry createdEntry2 = SugarRecord.findById(ListEntry.class, returnedEntry2.getId());
        assertEquals(createdEntry2, returnedEntry2);
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
        assertEquals(true, changedFirstListEntry.mStruck);

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
        ShoppingList savedList = SugarRecord.findById(ShoppingList.class, returnedList.getId());
        assertNotNull(savedList);
        assertEquals(returnedList, savedList);
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
}