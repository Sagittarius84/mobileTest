package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.UnitProvider;

public class IUnitControllerTest extends AndroidTestCase {

    Unit mLiter;
    Unit mGram;
    Unit mMeter;
    Product mMilk;
    Product mShelf;

    IUnitController mUnitController;
    IProductController mProductController;
    ContentResolver mResolver;

    public void setUp() throws Exception {
        super.setUp();
        mResolver = mContext.getContentResolver();
        mUnitController = ControllerFactory.getUnitController(mContext);
        mProductController = ControllerFactory.getProductController(mContext);
        tearDown();

        mLiter = mUnitController.createUnit("_TEST_liter");
        mGram = mUnitController.createUnit("_TEST_gram");
        mMeter = mUnitController.createUnit("_TEST_meter");

        assertNotNull(mLiter);
        assertNotNull(mGram);
        assertNotNull(mMeter);


        mMilk = mProductController.createProduct("_TEST_milk", mLiter, 1.0f, 1.0f);
        mShelf = mProductController.createProduct("_TEST_shelf", mMeter, 1.0f, 1.0f);

        assertNotNull(mMilk);
        assertNotNull(mShelf);

    }

    public void tearDown() throws Exception {
        mResolver.delete(Uri.parse(UnitProvider.MULTIPLE_UNIT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), null, null);
    }

    public void testCreateUnit() throws Exception {
        assertNull(mUnitController.createUnit(null));
        assertNull(mUnitController.createUnit("_TEST_gram"));

        Unit returnedUnit = mUnitController.createUnit("_TEST_pound");
        assertNotNull(returnedUnit);
        assertEquals("_TEST_pound", returnedUnit.mName);
        Unit savedUnit = mUnitController.findById(returnedUnit.mUUID);
        assertNotNull(savedUnit);
        assertEquals(returnedUnit, savedUnit);
    }

    public void testRenameUnit() throws Exception {
        assertNull(mUnitController.renameUnit(null, "_TEST_shouldNotExist"));

        Unit returnedUnchangedUnit = mUnitController.renameUnit(mGram, "_TEST_liter");
        assertNotNull(returnedUnchangedUnit);
        assertEquals(mGram, returnedUnchangedUnit);
        assertEquals(returnedUnchangedUnit, mUnitController.findById(returnedUnchangedUnit.mUUID));

        Unit returnedChangedUnit = mUnitController.renameUnit(mGram, "_TEST_pound");
        assertNotNull(returnedChangedUnit);
        assertEquals("_TEST_pound", returnedChangedUnit.mName);
        assertEquals(returnedChangedUnit, mUnitController.findById(returnedChangedUnit.mUUID));
    }

    public void testDeleteUnit() throws Exception {
        assertTrue(mUnitController.deleteUnit(null, IUnitController.MODE_DELETE_REFERENCES));

        assertFalse(mUnitController.deleteUnit(mLiter, IUnitController.MODE_BREAK_DELETION));
        assertTrue(mUnitController.deleteUnit(mGram, IUnitController.MODE_BREAK_DELETION));

        assertTrue(mUnitController.deleteUnit(mLiter, IUnitController.MODE_DELETE_REFERENCES));
        assertNull(mProductController.findById(mMilk.mUUID));

        assertTrue(mUnitController.deleteUnit(mMeter, IUnitController.MODE_UNLINK_REFERENCES));
        Product changedProduct = mProductController.findById(mShelf.mUUID);
        assertNotNull(changedProduct);
        if (changedProduct.mUnit != null) {
            assertEquals("-", changedProduct.mUnit.mUUID);
        } else {
            assertNull(changedProduct.mUnit);
        }
    }
}