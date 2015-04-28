package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;

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
        fail("This test is a stub");
    }

    public void testModifyProduct() throws Exception {
        fail("This test is a stub");
    }

    public void testRemoveProduct() throws Exception {
        fail("This test is a stub");
    }

    public void testAddTagToProduct() throws Exception {
        fail("This test is a stub");
    }

    public void testRemoveTagFromProduct() throws Exception {
        fail("This test is a stub");
    }
}
