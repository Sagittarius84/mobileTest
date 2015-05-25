package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

public class ICategoryControllerTest extends AndroidTestCase {

    private ICategoryController mCategoryController;
    private Category mCategoryWork;
    private ShoppingList mListHardwareStore;

    public void setUp() throws Exception {
        super.setUp();

        mCategoryWork = new Category("_TEST_work");
        mCategoryWork.save();
        mListHardwareStore = new ShoppingList("_TEST_hardware store", mCategoryWork);
        mListHardwareStore.save();
        mCategoryWork.save();

        mCategoryController = ControllerFactory.getCategoryController();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(ShoppingList.class, ShoppingList.ATTR_CATEGORY + " = ?",
                mCategoryWork.getId() + "");

        SugarRecord.deleteAll(ShoppingList.class, ShoppingList.ATTR_NAME + " LIKE '_TEST_%'");
        SugarRecord.deleteAll(Category.class, Category.ATTR_NAME + " LIKE '_TEST_%'");
    }

    public void testCreateCategory() throws Exception {
        assertNull(mCategoryController.createCategory(null));
        assertNull(mCategoryController.createCategory(""));
        assertNull(mCategoryController.createCategory("_TEST_work"));

        Category returnedCreatedCategory1 = mCategoryController.createCategory("_TEST_home");
        assertNotNull(returnedCreatedCategory1);
        assertEquals("_TEST_home", returnedCreatedCategory1.mName);
        assertEquals(returnedCreatedCategory1,
                SugarRecord.findById(Category.class, returnedCreatedCategory1.getId()));
    }

    public void testRenameCategory() throws Exception {
        assertNull(mCategoryController.renameCategory(null, ""));
        Category notSavedCategory = new Category("_TEST_not saved category");
        assertNull(mCategoryController.renameCategory(notSavedCategory, "_TEST_still not saved category"));
        assertEquals(mCategoryWork, mCategoryController.renameCategory(mCategoryWork, ""));

        Category returnedRenamedCategory1 = mCategoryController.renameCategory(mCategoryWork, "_TEST_home");
        assertNotNull(returnedRenamedCategory1);
        assertEquals("_TEST_home", returnedRenamedCategory1.mName);
        assertEquals(returnedRenamedCategory1, SugarRecord.findById(Category.class, mCategoryWork.getId()));
    }

    public void testRemoveCategory() throws Exception {
        // Nothing should happen if a wrong input is given.
        mCategoryController.removeCategory(null);
        Category notSavedCategory = new Category("_TEST_not saved category");
        mCategoryController.removeCategory(notSavedCategory);

        // If a category gets deleted, lists have to be unlinked.
        long deletedId = mCategoryWork.getId();
        mCategoryController.removeCategory(mCategoryWork);
        assertNull(SugarRecord.findById(Category.class, deletedId));
        assertNull(SugarRecord.findById(ShoppingList.class, mListHardwareStore.getId()).mCategory);
    }
}