package org.noorganization.instalist.provider.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.ProviderTestUtils;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.UnitProvider;

/**
 * Tests for {@link org.noorganization.instalist.provider.ProviderTestUtils}
 * Created by tinos_000 on 16.11.2015.
 */
public class ProviderTestUtilsTest extends AndroidTestCase {

    private ContentResolver mResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResolver = mContext.getContentResolver();
        tearDown();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mResolver.delete(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(UnitProvider.MULTIPLE_UNIT_CONTENT_URI), null, null);
    }

    public void testInsertUnit() {
        Uri newUnitUri = ProviderTestUtils.insertUnit(mResolver, "g");
        assertNotNull(newUnitUri);

        Cursor unitCursor = mResolver.query(newUnitUri, Unit.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(unitCursor);
        assertEquals(1, unitCursor.getCount());
        unitCursor.moveToFirst();
        assertEquals("g", unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.NAME)));
        unitCursor.close();
    }

    public void testGetUnit() {
        Uri newUnitUri = ProviderTestUtils.insertUnit(mResolver, "g");
        assertNotNull(newUnitUri);

        Unit unit = ProviderTestUtils.getUnit(mResolver, newUnitUri);
        assertNotNull(unit);
        assertEquals("g", unit.mName);
    }

    public void testInsertTag() {
        Uri newTagUri = ProviderTestUtils.insertTag(mResolver, "vegetable");
        assertNotNull(newTagUri);

        Cursor tagCursor = mResolver.query(newTagUri, Tag.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(tagCursor);
        assertEquals(1, tagCursor.getCount());
        tagCursor.moveToFirst();
        assertEquals("vegetable", tagCursor.getString(tagCursor.getColumnIndex(Tag.COLUMN.NAME)));
        tagCursor.close();
    }

    public void testGetTag() {
        Uri newTagUri = ProviderTestUtils.insertTag(mResolver, "vegetable");
        assertNotNull(newTagUri);

        Tag unit = ProviderTestUtils.getTag(mResolver, newTagUri);
        assertNotNull(unit);
        assertEquals("vegetable", unit.mName);
    }

    public void testInsertCategory() {
        Uri newCategoryUri = ProviderTestUtils.insertCategory(mResolver, "TEST_CATEGORY");
        assertNotNull(newCategoryUri);

        Cursor tagCursor = mResolver.query(newCategoryUri, Category.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(tagCursor);
        assertEquals(1, tagCursor.getCount());
        tagCursor.moveToFirst();
        assertEquals("TEST_CATEGORY", tagCursor.getString(tagCursor.getColumnIndex(Category.COLUMN.NAME)));
        tagCursor.close();
    }

    public void testInsertList() {
        Uri newCategoryUri = ProviderTestUtils.insertCategory(mResolver, "TEST_CATEGORY");
        assertNotNull(newCategoryUri);

        Uri newListUri = ProviderTestUtils.insertList(mResolver, newCategoryUri.getLastPathSegment(), "TEST_LIST");
        assertNotNull(newListUri);

        Cursor listCursor = mResolver.query(newListUri, ShoppingList.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(listCursor);
        assertEquals(1, listCursor.getCount());
        listCursor.moveToFirst();
        assertEquals("TEST_LIST", listCursor.getString(listCursor.getColumnIndex(ShoppingList.COLUMN.NAME)));
        listCursor.close();
    }

    public void testInserListEntry() {
        Uri newCategoryUri = ProviderTestUtils.insertCategory(mResolver, "TEST_CATEGORY");
        assertNotNull(newCategoryUri);

        Uri newListUri = ProviderTestUtils.insertList(mResolver, newCategoryUri.getLastPathSegment(), "TEST_LIST");
        assertNotNull(newListUri);

        Uri newProductUri = ProviderTestUtils.insertProduct(mResolver, "TEST_PRODUCT", 1.0f, 1.0f, null);
        assertNotNull(newProductUri);

        Uri listEntryUri = ProviderTestUtils.insertListEntry(mResolver,
                newCategoryUri.getLastPathSegment(),
                newListUri.getLastPathSegment(),
                newProductUri.getLastPathSegment(),
                3.0f,
                false,
                1);
        assertNotNull(listEntryUri);

        Cursor listEntryCursor = mResolver.query(listEntryUri, ListEntry.COLUMN.ALL_COLUMNS, null, null, null);

        assertNotNull(listEntryCursor);
        assertEquals(1, listEntryCursor.getCount());
        listEntryCursor.moveToFirst();
        assertEquals(false, listEntryCursor.getInt(listEntryCursor.getColumnIndex(ListEntry.COLUMN.STRUCK)) == 1);
        assertEquals(1, listEntryCursor.getInt(listEntryCursor.getColumnIndex(ListEntry.COLUMN.PRIORITY)));
        assertEquals(newProductUri.getLastPathSegment(), listEntryCursor.getString(listEntryCursor.getColumnIndex(ListEntry.COLUMN.PRODUCT)));
        assertEquals(newListUri.getLastPathSegment(), listEntryCursor.getString(listEntryCursor.getColumnIndex(ListEntry.COLUMN.LIST)));
        assertEquals(3.0f, listEntryCursor.getFloat(listEntryCursor.getColumnIndex(ListEntry.COLUMN.AMOUNT)), 0.001f);
        listEntryCursor.close();
    }

    public void testInsertProduct() {
        Uri newUnitUri = ProviderTestUtils.insertUnit(mResolver, "g");
        Uri insertedProductUri = ProviderTestUtils.insertProduct(mResolver, "TEST_PRODUCT", 1.0f, 0.5f, newUnitUri.getLastPathSegment());

        Cursor productCursor = mResolver.query(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), Product.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());

        productCursor.moveToFirst();
        assertEquals("TEST_PRODUCT", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.NAME)));
        assertEquals(1.0f, productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.DEFAULT_AMOUNT)), 0.0001f);
        assertEquals(0.5f, productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.STEP_AMOUNT)), 0.0001f);
        assertEquals(insertedProductUri.getLastPathSegment(), productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.ID)));
        assertEquals(newUnitUri.getLastPathSegment(), productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.UNIT)));

        productCursor.close();
    }

    public void testInsertProductWithoutUnit() {
        Uri insertedProductUri = ProviderTestUtils.insertProduct(mResolver, "TEST_PRODUCT", 1.0f, 0.5f, null);

        Cursor productCursor = mResolver.query(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), Product.COLUMN.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());

        productCursor.moveToFirst();
        assertEquals("TEST_PRODUCT", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.NAME)));
        assertEquals(1.0f, productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.DEFAULT_AMOUNT)), 0.0001f);
        assertEquals(0.5f, productCursor.getFloat(productCursor.getColumnIndex(Product.COLUMN.STEP_AMOUNT)), 0.0001f);
        assertEquals(insertedProductUri.getLastPathSegment(), productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.ID)));
        assertEquals(null, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN.UNIT)));

        productCursor.close();
    }

    public void testGetProduct() {
        Uri newUnitUri = ProviderTestUtils.insertUnit(mResolver, "g");
        Uri insertedProduct = ProviderTestUtils.insertProduct(mResolver, "TEST_PRODUCT", 1.0f, 0.5f, newUnitUri.getLastPathSegment());

        Product product = ProviderTestUtils.getProduct(mResolver, insertedProduct.getLastPathSegment());
        assertEquals("TEST_PRODUCT", product.mName);
        assertEquals(1.0f, product.mDefaultAmount, 0.0001f);
        assertEquals(0.5f, product.mStepAmount, 0.0001f);
        assertEquals(newUnitUri.getLastPathSegment(), product.mUnit.mUUID);
    }


}
