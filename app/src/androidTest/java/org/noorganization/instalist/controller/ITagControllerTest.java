package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;
import android.util.Log;

import com.orm.SugarRecord;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;

public class ITagControllerTest extends AndroidTestCase {

    static final String TEST_MILK_PRODUCT = "_TEST_milkProduct";

    Tag mMetalware;
    Tag mMilkProduct;
    Product mCheese;
    TaggedProduct mCheeseMilkProductTag;
    ITagController mTagController;

    public void setUp() throws Exception {
        super.setUp();

        mMetalware = new Tag("_TEST_metalware");
        mMetalware.save();

        mMilkProduct = new Tag(TEST_MILK_PRODUCT);
        mMilkProduct.save();

        mCheese = new Product("_TEST_cheese", null);
        mCheese.save();

        mCheeseMilkProductTag = new TaggedProduct(mMilkProduct, mCheese);
        mCheeseMilkProductTag.save();

        mTagController = ControllerFactory.getTagController();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(TaggedProduct.class, "m_product = ? or m_tag = ? or m_tag = ?",
                mCheese.getId() + "", mMilkProduct.getId() + "", mMetalware.getId() + "");
        SugarRecord.deleteAll(Product.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Tag.class, "m_name LIKE '_TEST_%'");
    }

    public void testCreateTag() throws Exception {
        assertNull(mTagController.createTag(null));
        assertNull(mTagController.createTag(TEST_MILK_PRODUCT));

        Tag createdTag = mTagController.createTag("_TEST_vegetable");
        assertNotNull(createdTag);
        assertEquals("_TEST_vegetable", createdTag.mName);
        assertEquals(createdTag, SugarRecord.findById(Tag.class, createdTag.getId()));
    }

    public void testRenameTag() throws Exception {
        assertNull(mTagController.renameTag(null, "_TEST_somethingThatShouldNotWork"));
        assertEquals(mMilkProduct, mTagController.renameTag(mMilkProduct, null));
        assertEquals(mMilkProduct, mTagController.renameTag(mMilkProduct, "_TEST_metalware"));

        Tag renamedTag = mTagController.renameTag(mMilkProduct, "_TEST_vegetable");
        assertEquals("_TEST_vegetable", renamedTag.mName);
        assertEquals(renamedTag, SugarRecord.findById(Tag.class, mMilkProduct.getId()));
    }

    public void testRemoveTag() throws Exception {
        mTagController.removeTag(mMilkProduct);
        assertNull(SugarRecord.findById(TaggedProduct.class, mCheeseMilkProductTag.getId()));
        assertNotNull(SugarRecord.findById(Product.class, mCheese.getId()));
        assertNotNull(SugarRecord.findById(Tag.class, mMetalware.getId()));
    }
}