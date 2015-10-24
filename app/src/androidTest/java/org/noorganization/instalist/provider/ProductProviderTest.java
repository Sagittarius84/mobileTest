package org.noorganization.instalist.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;

import java.util.UUID;

/**
 * Test for the {@link ProductProvider} provider.
 * Created by Tino on 24.10.2015.
 */
public class ProductProviderTest extends AndroidTestCase {

    IInternalProvider mProductProvider;
    SQLiteDatabase mDatabase;

    @Override
    public void setUp() {
        mDatabase = new DBOpenHelper(getContext(), null).getWritableDatabase();
        mProductProvider = new ProductProvider();
        mProductProvider.onCreate(mDatabase);
    }

    @Override
    public void tearDown() {
        mDatabase.close();
    }

    private void resetDb() {
        mDatabase.delete(Product.TABLE_NAME, null, null);
    }

    public void testQueryMultipleProducts() {
        Uri multipleProductsUri = Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI);
        Cursor noProduts = mProductProvider.query(multipleProductsUri, null, null, null, null);
        assertNotNull(noProduts);
        assertEquals(0, noProduts.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Product.TABLE_NAME + " VALUES (?,?,?,?,?)", new String[]{
                uuid,
                "TestProduct1",
                null,
                "0.5",
                null
        });

        Cursor productCursor = mProductProvider.query(multipleProductsUri, null, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestProduct1", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Product.TABLE_NAME + " VALUES (?,?,?,?,?)", new String[]{
                uuid2,
                "TestProduct2",
                null,
                "0.5",
                null
        });

        productCursor = mProductProvider.query(multipleProductsUri, null, null, null, Product.COLUMN_NAME + " ASC");
        assertNotNull(productCursor);
        assertEquals(2, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestProduct1", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_ID)));
        productCursor.moveToNext();
        assertEquals("TestProduct2", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_ID)));
        resetDb();
    }

    public void testQuerySingleProduct() {
        Cursor noCategory = mProductProvider.query(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*",
                UUID.randomUUID().toString())), null, null, null, null);

        assertNotNull(noCategory);
        assertEquals(0, noCategory.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Product.TABLE_NAME + " VALUES (?,?,?,?,?)", new String[]{
                uuid,
                "TestProduct1",
                null,
                "0.5",
                null
        });

        Cursor productCursor = mProductProvider.query(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*",
                uuid)), null, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestProduct1", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Product.TABLE_NAME + " VALUES (?,?,?,?,?)", new String[]{
                uuid2,
                "TestProduct2",
                null,
                "0.5",
                null
        });

        productCursor = mProductProvider.query(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuid2)),
                null, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestProduct2", productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Product.COLUMN_ID)));
        resetDb();
    }

    public void testGetTypeSingleProduct() {
        String type = mProductProvider.getType(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", "0")));
        assertEquals(type, ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ProductProvider.PRODUCT_BASE_TYPE);
    }

    public void testGetTypeMultipleProducts() {
        String type = mProductProvider.getType(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI));
        assertEquals(type, ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ProductProvider.PRODUCT_BASE_TYPE);
    }

    public void testInsertSingleProduct() {
        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        contentValues.put(Product.COLUMN_ID, uuid);
        contentValues.put(Product.COLUMN_NAME, "TestProduct");
        contentValues.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValues.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValues.put(Product.COLUMN_UNIT_ID, (String) null);

        Uri uri = mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuid)), contentValues);
        String pseudoUri = ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuid);
        assertNotNull(uri);
        assertEquals(pseudoUri, uri.toString());

        Cursor cursor = mProductProvider.query(uri, Product.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Product.COLUMN_ID)));
        assertEquals("TestProduct", cursor.getString(cursor.getColumnIndex(Product.COLUMN_NAME)));
        assertEquals(0.5f, cursor.getFloat(cursor.getColumnIndex(Product.COLUMN_DEFAULT_AMOUNT)));
        assertEquals(0.5f, cursor.getFloat(cursor.getColumnIndex(Product.COLUMN_STEP_AMOUNT)));
        assertEquals(null, cursor.getString(cursor.getColumnIndex(Product.COLUMN_UNIT_ID)));
        resetDb();
    }
}
