package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.provider.InstalistProvider;

import java.util.UUID;

public class ICategoryControllerTest extends AndroidTestCase {

    private ICategoryController mCategoryController;
    private Category mCategoryWork;
    private ShoppingList mListHardwareStore;
    private ContentResolver mResolver;

    public void setUp() throws Exception {
        super.setUp();

        mResolver = getContext().getContentResolver();
        ContentValues testWorkCatCV = new ContentValues(1);
        testWorkCatCV.put(Category.COLUMN.NAME, "_TEST_work");
        Uri createdCat = mResolver.insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                testWorkCatCV);
        mCategoryWork = new Category(createdCat.getLastPathSegment(), "_TEST_work");
        ContentValues testHWStoreListCV = new ContentValues(2);
        testHWStoreListCV.put(ShoppingList.COLUMN.NAME, "_TEST_hardware store");
        testHWStoreListCV.put(ShoppingList.COLUMN.CATEGORY,
                createdCat.getLastPathSegment());
        Uri createdList = mResolver.insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" +
                        createdCat.getLastPathSegment() + "/list"),
                testHWStoreListCV);
        mListHardwareStore = new ShoppingList(createdList.getLastPathSegment(),
                "_TEST_hardware store", mCategoryWork);

        mCategoryController = ControllerFactory.getCategoryController(mContext);
    }

    public void tearDown() throws Exception {
        Cursor catsToDel = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                new String[]{ Category.COLUMN.ID},
                Category.COLUMN.NAME + " LIKE '_TEST_%'",
                null,
                null);
        catsToDel.moveToFirst();
        while (!catsToDel.isAfterLast()) {
            String catUUIDStr = catsToDel.getString(catsToDel.getColumnIndex(
                    Category.COLUMN.ID));
            Cursor listsToDel = mResolver.query(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" +
                            catUUIDStr + "/list"),
                    new String[]{ ShoppingList.COLUMN.ID},
                    ShoppingList.COLUMN.NAME + " LIKE '_TEST_%'",
                    null,
                    null);
            listsToDel.moveToFirst();
            while (!listsToDel.isAfterLast()) {
                String listUUIDStr = listsToDel.getString(listsToDel.getColumnIndex(
                        ShoppingList.COLUMN.ID));
                mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                        "category/" + catUUIDStr + "/list/" + listUUIDStr), null, null);
                listsToDel.moveToNext();
            }
            listsToDel.close();
            mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                    "category/" + catUUIDStr), null, null);
            catsToDel.moveToNext();
        }
        catsToDel.close();

        Cursor listsToDel = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list"),
                new String[]{ShoppingList.COLUMN.ID},
                ShoppingList.COLUMN.NAME + " LIKE '_TEST_%'",
                null,
                null);
        listsToDel.moveToFirst();
        while (!listsToDel.isAfterLast()) {
            String listUUIDStr = listsToDel.getString(listsToDel.getColumnIndex(
                    ShoppingList.COLUMN.ID));
            mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                    "category/-/list/" + listUUIDStr), null, null);
            listsToDel.moveToNext();
        }
        listsToDel.close();
    }

    public void testCreateCategory() throws Exception {
        assertNull(mCategoryController.createCategory(null));
        assertNull(mCategoryController.createCategory(""));
        assertNull(mCategoryController.createCategory("_TEST_work"));

        Category returnedCreatedCategory1 = mCategoryController.createCategory("_TEST_home");
        assertNotNull(returnedCreatedCategory1);
        assertEquals("_TEST_home", returnedCreatedCategory1.mName);
        assertNotNull(returnedCreatedCategory1.mUUID);
        Cursor catCheck = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" +
                        returnedCreatedCategory1.mUUID.toString()), null, null, null, null);
        assertEquals(1, catCheck.getCount());
        catCheck.moveToFirst();
        assertEquals("_TEST_home", catCheck.getString(catCheck.getColumnIndex(
                Category.COLUMN.NAME)));
        catCheck.close();
    }

    public void testGetCategoryById() throws Exception {
        assertNull(mCategoryController.getCategoryByID(null));
        assertNull(mCategoryController.getCategoryByID(UUID.randomUUID().toString()));

        assertEquals(mCategoryWork, mCategoryController.getCategoryByID(mCategoryWork.mUUID));
    }

    public void testRenameCategory() throws Exception {
        assertNull(mCategoryController.renameCategory(null, ""));
        Category notSavedCategory = new Category(UUID.randomUUID().toString(), "_TEST_not saved category");
        assertNull(mCategoryController.renameCategory(notSavedCategory, "_TEST_still not saved category"));
        assertEquals(mCategoryWork, mCategoryController.renameCategory(mCategoryWork, ""));

        Category returnedRenamedCategory1 = mCategoryController.renameCategory(mCategoryWork, "_TEST_home");
        assertNotNull(returnedRenamedCategory1);
        assertEquals("_TEST_home", returnedRenamedCategory1.mName);
        Cursor catCheck = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" +
                        returnedRenamedCategory1.mUUID.toString()), null, null, null, null);
        assertEquals(1, catCheck.getCount());
        catCheck.moveToFirst();
        assertEquals("_TEST_home", catCheck.getString(catCheck.getColumnIndex(
                Category.COLUMN.NAME)));
        catCheck.close();
    }

    public void testRemoveCategory() throws Exception {
        // Nothing should happen if a wrong input is given.
        mCategoryController.removeCategory(null);
        Category notSavedCategory = new Category(UUID.randomUUID().toString(), "_TEST_not saved category");
        mCategoryController.removeCategory(notSavedCategory);

        // If a category gets deleted, lists have to be unlinked.
        String deletedId = mCategoryWork.mUUID;
        mCategoryController.removeCategory(mCategoryWork);
        Cursor catCheck = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" +
                        deletedId.toString()), null, null, null, null);
        assertEquals(0, catCheck.getCount());
        catCheck.close();
        Cursor listCheck = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "category/-/list/" + mListHardwareStore.mUUID.toString()), null, null, null, null);
        assertEquals(1, listCheck.getCount());
        listCheck.moveToFirst();
        assertNull(listCheck.getString(listCheck.getColumnIndex(
                ShoppingList.COLUMN.CATEGORY)));
        listCheck.close();

    }
}