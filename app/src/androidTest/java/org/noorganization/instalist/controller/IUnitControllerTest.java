package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Unit;

public class IUnitControllerTest extends AndroidTestCase {

    Unit mLiter;
    Unit mGram;
    Unit mMeter;
    Product mMilk;
    Product mShelf;

    IUnitController mUnitController;

    public void setUp() throws Exception {
        super.setUp();

        mLiter = new Unit("_TEST_liter");
        mLiter.save();
        mGram = new Unit("_TEST_gram");
        mGram.save();
        mMeter = new Unit("_TEST_meter");
        mMeter.save();

        mMilk = new Product("_TEST_milk", mLiter);
        mMilk.save();
        mShelf = new Product("_TEST_shelf", mMeter);
        mShelf.save();

        mUnitController = ControllerFactory.getUnitController();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(Unit.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Product.class, "m_name LIKE '_TEST_%'");
    }

    public void testCreateUnit() throws Exception {
        assertNull(mUnitController.createUnit(null));
        assertNull(mUnitController.createUnit("_TEST_gram"));

        Unit returnedUnit = mUnitController.createUnit("_TEST_pound");
        assertNotNull(returnedUnit);
        assertEquals("_TEST_pound", returnedUnit.mName);
        Unit savedUnit = SugarRecord.findById(Unit.class, returnedUnit.getId());
        assertNotNull(savedUnit);
        assertEquals(returnedUnit, savedUnit);
    }

    public void testRenameUnit() throws Exception {
        assertNull(mUnitController.renameUnit(null, "_TEST_shouldNotExist"));

        Unit returnedUnchangedUnit = mUnitController.renameUnit(mGram, "_TEST_liter");
        assertNotNull(returnedUnchangedUnit);
        assertEquals(mGram, returnedUnchangedUnit);
        assertEquals(returnedUnchangedUnit, SugarRecord.findById(Unit.class, returnedUnchangedUnit.getId()));

        Unit returnedChangedUnit = mUnitController.renameUnit(mGram, "_TEST_pound");
        assertNotNull(returnedChangedUnit);
        assertEquals("_TEST_pound", returnedChangedUnit.mName);
        assertEquals(returnedChangedUnit, SugarRecord.findById(Unit.class, returnedChangedUnit.getId()));
    }

    public void testDeleteUnit() throws Exception {
        assertTrue(mUnitController.deleteUnit(null, IUnitController.MODE_DELETE_REFERENCES));

        assertFalse(mUnitController.deleteUnit(mLiter, IUnitController.MODE_BREAK_DELETION));
        assertTrue(mUnitController.deleteUnit(mGram, IUnitController.MODE_BREAK_DELETION));

        assertTrue(mUnitController.deleteUnit(mLiter, IUnitController.MODE_DELETE_REFERENCES));
        assertNull(SugarRecord.findById(Product.class, mMilk.getId()));

        assertTrue(mUnitController.deleteUnit(mMeter, IUnitController.MODE_UNLINK_REFERENCES));
        Product changedProduct = SugarRecord.findById(Product.class, mShelf.getId());
        assertNotNull(changedProduct);
        assertNull(changedProduct.mUnit);
    }
}