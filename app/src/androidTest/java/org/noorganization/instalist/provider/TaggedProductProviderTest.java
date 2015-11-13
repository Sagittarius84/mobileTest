package org.noorganization.instalist.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.TagProvider;
import org.noorganization.instalist.provider.internal.TaggedProductProvider;

import java.util.UUID;

/**
 * Tests for all possible methods of TaggedProvider.
 * Created by Tino on 28.10.2015.
 */
public class TaggedProductProviderTest extends AndroidTestCase {

    IInternalProvider mTaggedProductProvider;
    IInternalProvider mProductProvider;
    IInternalProvider mTagProvider;

    SQLiteDatabase mDatabase;

    @Override
    public void setUp() {
        mDatabase = new DBOpenHelper(getContext(), null).getWritableDatabase();
        mTaggedProductProvider = new TaggedProductProvider(getContext());
        mProductProvider = new ProductProvider(getContext());
        mTagProvider = new TagProvider(getContext());

        mProductProvider.onCreate(mDatabase);
        mTagProvider.onCreate(mDatabase);
        mTaggedProductProvider.onCreate(mDatabase);

    }

    @Override
    public void tearDown() {
        mDatabase.close();
    }

    private void resetDb() {
        mDatabase.delete(TaggedProduct.TABLE_NAME, null, null);
        mDatabase.delete(Tag.TABLE_NAME, null, null);
        mDatabase.delete(Product.TABLE_NAME, null, null);
    }


    public void testQuerySingleTaggedProduct() {

        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid, tagUuid, productUuid});

        Uri taggedProductUri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));

        Cursor cursor = mTaggedProductProvider.query(taggedProductUri, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();

        assertEquals(taggedProductUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals(tagUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
        assertEquals(productUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID)));
    }

    public void testQueryMultipleTaggedProduct() {


        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid, tagUuid, productUuid});
        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid2, tagUuid, productUuid2});
        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid3, tagUuid2, productUuid3});

        Uri taggedProductUri = Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI);

        Cursor cursor = mTaggedProductProvider.query(taggedProductUri, new String[]{TaggedProduct.COLUMN_PREFIXED.ID, TaggedProduct.COLUMN_PREFIXED.TAG_ID, TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID, Product.PREFIXED_COLUMN.NAME}, null, null, Product.PREFIXED_COLUMN.NAME + " ASC");
        assertNotNull(cursor);
        assertEquals(3, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(taggedProductUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals("Product1", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));
        assertEquals(tagUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
        assertEquals(productUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID)));

        assertTrue(cursor.moveToNext());

        assertEquals(taggedProductUuid2, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals("Product2", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));
        assertEquals(tagUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
        assertEquals(productUuid2, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID)));

        assertTrue(cursor.moveToNext());
        assertEquals(taggedProductUuid3, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals("Product3", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));
        assertEquals(tagUuid2, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
        assertEquals(productUuid3, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID)));
    }

    public void testQueryMultipleTaggedProductByTag() {

        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid, tagUuid, productUuid});
        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid2, tagUuid, productUuid2});
        mDatabase.execSQL("INSERT INTO " + TaggedProduct.TABLE_NAME + " VALUES (?,?,?)", new String[]{taggedProductUuid3, tagUuid2, productUuid3});

        Uri taggedProductUri = Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_BY_TAG_CONTENT_URI.replace("*", tagUuid));

        Cursor cursor = mTaggedProductProvider.query(taggedProductUri, TaggedProduct.ALL_COLUMNS_JOINED, null, null, Product.PREFIXED_COLUMN.NAME + " ASC");
        assertNotNull(cursor);
        assertEquals(2, cursor.getCount());

        cursor.moveToFirst();
        assertEquals("Product1", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));
        assertTrue(cursor.moveToNext());
        assertEquals("Product2", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));
    }


    public void testInsertSingleTaggedProduct() {


        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);


        Uri uri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));
        assertNotNull(uri);
        uri = mTaggedProductProvider.insert(uri, contentValues);
        assertNotNull(uri);
        assertEquals(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid), uri.toString());

        Uri uri2 = Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI);
        Cursor cursor = mTaggedProductProvider.query(uri, new String[]{TaggedProduct.COLUMN_PREFIXED.ID, TaggedProduct.COLUMN_PREFIXED.TAG_ID, TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID, Product.PREFIXED_COLUMN.NAME}, null, null, null);

        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        cursor.moveToNext();
        assertEquals(taggedProductUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals(tagUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
        assertEquals(productUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.PRODUCT_ID)));
        assertEquals("Product1", cursor.getString(cursor.getColumnIndex(Product.PREFIXED_COLUMN.NAME)));

    }


    public void testDeleteSingle(){
        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);


        Uri uri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));
        Uri uri2 = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid2));
        assertNotNull(uri);
        uri = mTaggedProductProvider.insert(uri, contentValues);

        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid2);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid2);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid2);
        uri2 = mTaggedProductProvider.insert(uri2, contentValues);

        Cursor cursor  = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null, null, null, null, null);
        assertEquals(2, cursor.getCount());

        int rowsAffected =  mTaggedProductProvider.delete(uri2, null, null);
        cursor = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null,null,null,null,null);
        assertEquals(1, rowsAffected);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(taggedProductUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
    }

    public void testDeleteMultiple(){
        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);


        Uri uri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));
        Uri uri2 = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid2));
        assertNotNull(uri);
        uri = mTaggedProductProvider.insert(uri, contentValues);

        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid2);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid2);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid2);
        uri2 = mTaggedProductProvider.insert(uri2, contentValues);

        Cursor cursor  = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null, null, null, null, null);
        assertEquals(2, cursor.getCount());

        int rowsAffected =  mTaggedProductProvider.delete(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI), null, null);
        cursor = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null,null,null,null,null);
        assertEquals(2, rowsAffected);
        assertEquals(0, cursor.getCount());

    }

    public void testDeleteMultipleByTag(){
        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);


        Uri uri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));
        Uri uri2 = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid2));
        assertNotNull(uri);
        uri = mTaggedProductProvider.insert(uri, contentValues);

        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid2);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid2);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid2);
        uri2 = mTaggedProductProvider.insert(uri2, contentValues);

        Cursor cursor  = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null, null, null, null, null);
        assertEquals(2, cursor.getCount());

        int rowsAffected =  mTaggedProductProvider.delete(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_BY_TAG_CONTENT_URI.replace("*", tagUuid2)), null, null);
        cursor = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null,null,null,null,null);
        assertEquals(1, rowsAffected);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(taggedProductUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
    }


    public void testUpdateSingle(){
        String productUuid = UUID.randomUUID().toString();
        String tagUuid = UUID.randomUUID().toString();
        String taggedProductUuid = UUID.randomUUID().toString();

        String productUuid2 = UUID.randomUUID().toString();
        String tagUuid2 = UUID.randomUUID().toString();
        String taggedProductUuid2 = UUID.randomUUID().toString();


        String productUuid3 = UUID.randomUUID().toString();
        String taggedProductUuid3 = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, productUuid, "Product1", 0.5f, 0.5f, (String) null);
        Uri tagUri = ProviderTestUtils.insertTag(mTagProvider, tagUuid, "Tag1");

        assertNotNull(productUri);
        assertNotNull(tagUri);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, productUuid2, "Product2", 0.5f, 0.5f, (String) null);
        Uri tagUri2 = ProviderTestUtils.insertTag(mTagProvider, tagUuid2, "Tag2");
        assertNotNull(productUri2);
        assertNotNull(tagUri2);
        Uri productUri3 = ProviderTestUtils.insertProduct(mProductProvider, productUuid3, "Product3", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri3);

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);


        Uri uri = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid));
        Uri uri2 = Uri.parse(TaggedProductProvider.SINGLE_TAGGED_PRODUCT_CONTENT_URI.replace("*", taggedProductUuid2));
        assertNotNull(uri);
        uri = mTaggedProductProvider.insert(uri, contentValues);

        contentValues.put(TaggedProduct.COLUMN.ID, taggedProductUuid2);
        contentValues.put(TaggedProduct.COLUMN.PRODUCT_ID, productUuid2);
        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid2);
        uri2 = mTaggedProductProvider.insert(uri2, contentValues);

        Cursor cursor  = mDatabase.query(TaggedProduct.TABLE_NAME, TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null, null, null, null, null);
        assertEquals(2, cursor.getCount());

        contentValues.put(TaggedProduct.COLUMN.TAG_ID, tagUuid);
        int rowsAffected =  mTaggedProductProvider.update(uri2, contentValues, null, null);
        cursor = mTaggedProductProvider.query(Uri.parse(TaggedProductProvider.MULTIPLE_TAGGED_PRODUCT_CONTENT_URI), TaggedProduct.COLUMN_PREFIXED.ALL_COLUMNS, null,null, Product.PREFIXED_COLUMN.NAME + " ASC");
        assertEquals(1, rowsAffected);
        assertEquals(2, cursor.getCount());
        cursor.moveToFirst();
        cursor.moveToNext();
        assertEquals(taggedProductUuid2, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.ID)));
        assertEquals(tagUuid, cursor.getString(cursor.getColumnIndex(TaggedProduct.COLUMN_PREFIXED.TAG_ID)));
    }
}
