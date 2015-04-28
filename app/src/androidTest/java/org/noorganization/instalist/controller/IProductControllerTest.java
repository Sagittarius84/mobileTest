package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.implementation.ListController;
import org.noorganization.instalist.controller.implementation.ProductController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;

public class IProductControllerTest extends AndroidTestCase {

    Unit mLiter;
    Product mMilk;
    Product mBroccoli;
    ShoppingList mList;
    Tag mTag;

    IProductController mController2Test;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mLiter = new Unit("_TEST_l");
        mLiter.save();
        mMilk = new Product("_TEST_milk", mLiter);
        mMilk.save();
        mBroccoli = new Product("_TEST_broccoli", null);
        mBroccoli.save();
        mTag = new Tag("_TEST_vegetable");
        mTag.save();

        mList = new ShoppingList("_TEST_home");
        mList.save();

        ListController.getInstance().addOrChangeItem(mList, mMilk, 1.0f);

        new TaggedProduct(mTag, mBroccoli).save();

        mController2Test = ProductController.getInstance();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        SugarRecord.deleteAll(ListEntry.class,
                "m_list = ? or m_product = ?",
                mList.getId() + "",
                mMilk.getId() + "");

        SugarRecord.deleteAll(TaggedProduct.class,
                "m_tag = ? or m_product = ?",
                mTag.getId() + "",
                mBroccoli.getId() + "");

        SugarRecord.deleteAll(ShoppingList.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Product.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Unit.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Tag.class, "m_name LIKE '_TEST_%'");
    }

    public void testCreateProduct() throws Exception {
        // Should not work because milk already exists.
        assertNull(mController2Test.createProduct("_TEST_milk", mLiter, 1.0f, 1.0f));
        // Should not work because a parameter is not ok.
        assertNull(mController2Test.createProduct("_TEST_butter", mLiter, 0.0f, 1.0f));

        Product createdProduct = mController2Test.createProduct("_TEST_butter", null, 1.0f, 0.25f);
        assertNotNull(createdProduct);
        Product savedProduct = SugarRecord.findById(Product.class, createdProduct.getId());
        assertNotNull(savedProduct);
        assertEquals(createdProduct, savedProduct);
        assertEquals("_TEST_butter", createdProduct.mName);
        assertNull(createdProduct.mUnit);
        assertEquals(1.0f, createdProduct.mDefaultAmount, 0.001f);
        assertEquals(0.25f, createdProduct.mStepAmount, 0.001f);
    }

    public void testModifyProduct() throws Exception {
        mMilk.mDefaultAmount = -200.0f;
        Product returnedProduct = mController2Test.modifyProduct(mMilk);
        assertFalse(mMilk.equals(returnedProduct));
        assertNotNull(returnedProduct);
        assertEquals(1.0f, returnedProduct.mDefaultAmount, 0.001f);

        mMilk.mDefaultAmount = 200.0f;
        returnedProduct = mController2Test.modifyProduct(mMilk);
        assertEquals(returnedProduct, mMilk);
    }

    public void testRemoveProduct() throws Exception {
        assertFalse(mController2Test.removeProduct(mMilk, false));
        assertNotNull(SugarRecord.findById(Product.class, mMilk.getId()));
        assertEquals(1, Select.from(ListEntry.class).where(
                Condition.prop("m_product").eq(mMilk.getId())).count());

        assertTrue(mController2Test.removeProduct(mBroccoli, false));
        assertNull(SugarRecord.findById(Product.class, mBroccoli.getId()));
        assertEquals(0, Select.from(TaggedProduct.class).where(
                Condition.prop("m_product").eq(mBroccoli.getId())).count());

        assertFalse(mController2Test.removeProduct(mMilk, true));
        assertNull(SugarRecord.findById(Product.class, mMilk.getId()));
        assertEquals(0, Select.from(ListEntry.class).where(
                Condition.prop("m_product").eq(mMilk.getId())).count());

    }

    public void testAddTagToProduct() throws Exception {
        assertTrue(mController2Test.addTagToProduct(mMilk, mTag));
        assertNotNull(Select.from(TaggedProduct.class).
                where(Condition.prop("m_product").eq(mMilk.getId())).
                and(Condition.prop("m_tag").eq(mTag.getId())).first());
    }

    public void testRemoveTagFromProduct() throws Exception {
        mController2Test.removeTagFromProduct(mBroccoli, mTag);

        assertEquals(0, Select.from(TaggedProduct.class).
                where(Condition.prop("m_product").eq(mBroccoli.getId())).
                and(Condition.prop("m_tag").eq(mTag.getId())).count());
    }
}
