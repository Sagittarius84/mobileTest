package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.TagProvider;
import org.noorganization.instalist.provider.internal.TaggedProductProvider;
import org.noorganization.instalist.provider.internal.UnitProvider;

public class IProductControllerTest extends AndroidTestCase {

    Unit mLiter;
    Product mMilk;
    Product mBroccoli;
    ShoppingList mList;
    Tag mTag;

    IProductController mController2Test;
    IUnitController mUnitController;
    ITagController mTagController;
    IListController mListController;

    ContentResolver mResolver;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mResolver = mContext.getContentResolver();
        mUnitController = ControllerFactory.getUnitController(mContext);
        mController2Test = ControllerFactory.getProductController(mContext);
        mTagController = ControllerFactory.getTagController(mContext);
        mListController = ControllerFactory.getListController(mContext);

        mLiter = mUnitController.createUnit("_TEST_l");
        assertNotNull(mLiter);

        mMilk = mController2Test.createProduct("_TEST_milk", mLiter, 1.0f, 1.0f);
        assertNotNull(mMilk);

        mBroccoli = mController2Test.createProduct("_TEST_broccoli", null, 1.0f, 1.0f);
        assertNotNull(mBroccoli);

        mTag = mTagController.createTag("_TEST_vegetable");
        assertNotNull(mTag);

        mList = mListController.addList("_TEST_home");
        assertNotNull(mList);
        ListEntry entry = mListController.addOrChangeItem(mList, mMilk, 1.0f);
        assertNotNull(entry);
        assertNotNull(mController2Test.addTagToProduct(mBroccoli, mTag));

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"), null, null);
        mResolver.delete(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI), null, null);

        mResolver.delete(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "list"), null, null);
        mResolver.delete(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(UnitProvider.MULTIPLE_UNIT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(TagProvider.MULTIPLE_TAG_CONTENT_URI), null, null);
    }

    public void testCreateProduct() throws Exception {
        // Should not work because milk already exists.
        assertNull(mController2Test.createProduct("_TEST_milk", mLiter, 1.0f, 1.0f));
        // Should not work because a parameter is not ok.
        assertNull(mController2Test.createProduct("_TEST_butter", mLiter, 0.0f, 1.0f));

        Product createdProduct = mController2Test.createProduct("_TEST_butter", null, 1.0f, 0.25f);
        assertNotNull(createdProduct);
        Product savedProduct = mController2Test.findById(createdProduct.mUUID);
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
        assertEquals(mMilk, returnedProduct);
    }

    public void testRemoveProduct() throws Exception {
        assertFalse(mController2Test.removeProduct(mMilk, false));
        assertNotNull(mController2Test.findById(mMilk.mUUID));

        Cursor listEntryCursor = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"),
                ListEntry.COLUMN.ALL_COLUMNS,
                ListEntry.COLUMN.PRODUCT + "=?",
                new String[]{mMilk.mUUID},
                null
        );
        assertNotNull(listEntryCursor);
        assertEquals(1, listEntryCursor.getCount());
        listEntryCursor.close();

        assertTrue(mController2Test.removeProduct(mBroccoli, false));
        assertNull(mController2Test.findById(mBroccoli.mUUID));

        Cursor taggedProductCursor = mResolver.query(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI),
                TaggedProduct.ALL_COLUMNS_JOINED,
                TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID + "=?",
                new String[]{mBroccoli.mUUID},
                null
        );
        assertNotNull(taggedProductCursor);

        assertEquals(0, taggedProductCursor.getCount());
        taggedProductCursor.close();

        assertTrue(mController2Test.removeProduct(mMilk, true));
        assertNull(mController2Test.findById(mMilk.mUUID));

        listEntryCursor = mResolver.query(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "entry"),
                ListEntry.COLUMN.ALL_COLUMNS,
                ListEntry.COLUMN.PRODUCT + "=?",
                new String[]{mMilk.mUUID},
                null
        );
        assertNotNull(listEntryCursor);
        assertEquals(1, listEntryCursor.getCount());
        listEntryCursor.close();

    }

    public void testAddTagToProduct() throws Exception {
        assertNotNull(mController2Test.addTagToProduct(mMilk, mTag));

        Cursor taggedProductCursor = mResolver.query(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI),
                TaggedProduct.ALL_COLUMNS_JOINED,
                TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID + "=? AND " + TaggedProduct.COLUMN_PREFIXED.TAG_ID + "=?",
                new String[]{mMilk.mUUID, mTag.mUUID},
                null
        );
        assertNotNull(taggedProductCursor);
        assertEquals(0, taggedProductCursor.getCount());
    }

    public void testRemoveTagFromProduct() throws Exception {
        mController2Test.removeTagFromProduct(mBroccoli, mTag);
        Cursor taggedProductCursor = mResolver.query(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI),
                TaggedProduct.ALL_COLUMNS_JOINED,
                TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID + "=? AND " + TaggedProduct.COLUMN_PREFIXED.TAG_ID + "=?",
                new String[]{mBroccoli.mUUID, mTag.mUUID},
                null
        );
        assertNotNull(taggedProductCursor);
        assertEquals(0, taggedProductCursor.getCount());
    }
}
