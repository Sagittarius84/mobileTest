package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

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
        mCategoryWork.save();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(ShoppingList.class, ShoppingList.ATTR_CATEGORY + " = ?",
                mCategoryWork.getId() + "");

        SugarRecord.deleteAll(ShoppingList.class, ShoppingList.ATTR_NAME + " LIKE '_TEST_%'");
        SugarRecord.deleteAll(Category.class, Category.ATTR_NAME + " LIKE '_TEST_%'");
    }

    public void testCreateCategory() throws Exception {

        fail("This test is a stub.");
    }

    public void testRenameCategory() throws Exception {

        fail("This test is a stub.");
    }

    public void testRemoveCategory() throws Exception {

        fail("This test is a stub.");
    }
}