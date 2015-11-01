package org.noorganization.instalist.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.provider.internal.CategoryProvider;
import org.noorganization.instalist.provider.internal.IInternalProvider;

import java.util.UUID;

/**
 * Created by damihe on 24.10.15.
 */
public class CategoryProviderTest extends AndroidTestCase {

    IInternalProvider mCategoryProvider;
    SQLiteDatabase mDatabase;

    @Override
    public void setUp() {
        mDatabase = new DBOpenHelper(getContext(), null).getWritableDatabase();
        mCategoryProvider = new CategoryProvider();
        mCategoryProvider.onCreate(mDatabase);
    }

    @Override
    public void tearDown() {
        mDatabase.close();
    }

    public void testQueryMultipleCategories() {
        Uri MULTIPLE_CATEGORIES = Uri.parse("content://" + InstalistProvider.AUTHORITY + "/category");
        Cursor noCategories = mCategoryProvider.query(MULTIPLE_CATEGORIES, null, null, null, null);
        assertNotNull(noCategories);
        assertEquals(0, noCategories.getCount());

        String stCategory = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category VALUES (?,?)", new String[]{
                stCategory,
                "TestCategory1"
        });
        Cursor oneCategory = mCategoryProvider.query(MULTIPLE_CATEGORIES, null, null, null, null);
        assertNotNull(oneCategory);
        assertEquals(1, oneCategory.getCount());
        oneCategory.moveToFirst();
        assertEquals("TestCategory1", oneCategory.getString(oneCategory.getColumnIndex("name")));
        assertEquals(stCategory, oneCategory.getString(oneCategory.getColumnIndex("_id")));

        String ndCategory = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category VALUES (?,?)", new String[]{
                ndCategory,
                "TestCategory2"
        });
        Cursor twoCategories = mCategoryProvider.query(MULTIPLE_CATEGORIES, null, null, null, "name ASC");
        assertNotNull(twoCategories);
        assertEquals(2, twoCategories.getCount());
        twoCategories.moveToFirst();
        assertEquals("TestCategory1", twoCategories.getString(twoCategories.getColumnIndex("name")));
        assertEquals(stCategory, twoCategories.getString(twoCategories.getColumnIndex("_id")));
        twoCategories.moveToNext();
        assertEquals("TestCategory2", twoCategories.getString(twoCategories.getColumnIndex("name")));
        assertEquals(ndCategory, twoCategories.getString(twoCategories.getColumnIndex("_id")));
    }

    public void testQuerySingleCategory() {
        String SINGLE_CATEGORY = "content://" + InstalistProvider.AUTHORITY + "/category/%s";
        Cursor noCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_CATEGORY,
                UUID.randomUUID().toString())), null, null, null, null);
        assertNotNull(noCategory);
        assertEquals(0, noCategory.getCount());

        String stCategory = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category VALUES (?,?)", new String[]{
                stCategory,
                "TestCategory1"
        });
        Cursor oneCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_CATEGORY,
                stCategory)), null, null, null, null);
        assertNotNull(oneCategory);
        assertEquals(1, oneCategory.getCount());
        oneCategory.moveToFirst();
        assertEquals("TestCategory1", oneCategory.getString(oneCategory.getColumnIndex("name")));
        assertEquals(stCategory, oneCategory.getString(oneCategory.getColumnIndex("_id")));

        String ndCategory = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category VALUES (?,?)", new String[]{
                ndCategory,
                "TestCategory2"
        });
        Cursor twoCategories = mCategoryProvider.query(Uri.parse(String.format(SINGLE_CATEGORY,
                ndCategory)), null, null, null, null);
        assertNotNull(twoCategories);
        assertEquals(1, twoCategories.getCount());
        twoCategories.moveToFirst();
        assertEquals("TestCategory2", twoCategories.getString(twoCategories.getColumnIndex("name")));
        assertEquals(ndCategory, twoCategories.getString(twoCategories.getColumnIndex("_id")));
    }

    public void testQueryMultipleLists() {
        Uri MULTIPLE_LISTS_NO_CATEGORY = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/-/list");
        String categoryUUID = UUID.randomUUID().toString();
        Uri MULTIPLE_LISTS_IN_CATEGORY = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/" + categoryUUID + "/list");

        Cursor notExistentCategory = mCategoryProvider.query(MULTIPLE_LISTS_IN_CATEGORY, null, null,
                null, null);
        assertNotNull(notExistentCategory);
        assertEquals(0, notExistentCategory.getCount());

        String noCatListUUID = UUID.randomUUID().toString();
        String inCatListUUID = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category (_id, name) VALUES (?, ?)", new String[]{
                categoryUUID, "a category"
        });
        mDatabase.execSQL("INSERT INTO list (_id, name, category) VALUES (?, ?, NULL), (?, ?, ?)",
                new String[]{
                        noCatListUUID, "test list without category",
                        inCatListUUID, "test list with category", categoryUUID
                });
        Cursor listNoCategory = mCategoryProvider.query(MULTIPLE_LISTS_NO_CATEGORY, null, null,
                null, null);
        assertNotNull(listNoCategory);
        assertEquals(1, listNoCategory.getCount());
        listNoCategory.moveToFirst();
        assertEquals(noCatListUUID, listNoCategory.getString(listNoCategory.getColumnIndex("_id")));
        assertEquals("test list without category", listNoCategory.getString(listNoCategory.getColumnIndex("name")));
        assertNull(listNoCategory.getString(listNoCategory.getColumnIndex("category")));

        Cursor listInCategory = mCategoryProvider.query(MULTIPLE_LISTS_IN_CATEGORY, null, null,
                null, null);
        assertNotNull(listInCategory);
        assertEquals(1, listInCategory.getCount());
        listInCategory.moveToFirst();
        assertEquals(inCatListUUID, listInCategory.getString(listInCategory.getColumnIndex("_id")));
        assertEquals("test list with category", listInCategory.getString(listInCategory.getColumnIndex("name")));
        assertEquals(categoryUUID, listInCategory.getString(listInCategory.getColumnIndex("category")));

    }

    public void testQuerySingleList() {
        String SINGLE_LIST = "content://" + InstalistProvider.AUTHORITY + "/category/%s/list/%s";
        Cursor noList = mCategoryProvider.query(Uri.parse(String.format(SINGLE_LIST,
                UUID.randomUUID().toString(), UUID.randomUUID().toString())), null, null, null, null);
        assertNotNull(noList);
        assertEquals(0, noList.getCount());

        String stList = UUID.randomUUID().toString();
        String ndList = UUID.randomUUID().toString();
        String categoryUUID = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO category VALUES (?,?)", new String[]{
                categoryUUID,
                "TestCategory1"
        });
        mDatabase.execSQL("INSERT INTO list (_id, name, category) VALUES (?, ?, null), (?, ?, ?)",
                new String[]{
                        stList, "listWOCategory",
                        ndList, "listWCategory", categoryUUID
                });
        Cursor stListWOCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_LIST,
                "-", stList)), null, null, null, null);
        assertNotNull(stListWOCategory);
        assertEquals(1, stListWOCategory.getCount());
        stListWOCategory.moveToFirst();
        assertEquals("listWOCategory", stListWOCategory.getString(stListWOCategory.getColumnIndex("name")));
        assertEquals(stList, stListWOCategory.getString(stListWOCategory.getColumnIndex("_id")));
        assertNull(stListWOCategory.getString(stListWOCategory.getColumnIndex("category")));

        Cursor ndListWCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_LIST,
                categoryUUID, ndList)), null, null, null, null);
        assertNotNull(ndListWCategory);
        assertEquals(1, ndListWCategory.getCount());
        ndListWCategory.moveToFirst();
        assertEquals("listWCategory", ndListWCategory.getString(ndListWCategory.getColumnIndex("name")));
        assertEquals(ndList, ndListWCategory.getString(ndListWCategory.getColumnIndex("_id")));
        assertEquals(categoryUUID, ndListWCategory.getString(ndListWCategory.getColumnIndex("category")));

        Cursor stListWCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_LIST,
                categoryUUID, stList)), null, null, null, null);
        assertNotNull(stListWCategory);
        assertEquals(0, stListWCategory.getCount());

        Cursor ndListWOCategory = mCategoryProvider.query(Uri.parse(String.format(SINGLE_LIST,
                "-", ndList)), null, null, null, null);
        assertNotNull(ndListWOCategory);
        assertEquals(0, ndListWOCategory.getCount());
    }

    public void testQueryMultipleListEntries() {
        String categoryUUID = UUID.randomUUID().toString();
        String listUUID = UUID.randomUUID().toString();
        Uri MULTIPLE_LIST_ELEMENTS = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/" + categoryUUID + "/list/" + listUUID + "/entry");
        Uri MULTIPLE_LIST_ELEMENTS_WO_CAT = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/-/list/" + listUUID + "/entry");

        mDatabase.execSQL("INSERT INTO category (_id, name) VALUES (?, ?)", new String[]{
                categoryUUID,
                "category"
        });
        mDatabase.execSQL("INSERT INTO list (_id, name, category) VALUES (?, ?, ?)", new String[]{
                listUUID,
                "list",
                categoryUUID
        });

        Cursor noEntries1 = mCategoryProvider.query(MULTIPLE_LIST_ELEMENTS, null, null, null, null);
        assertNotNull(noEntries1);
        assertEquals(0, noEntries1.getCount());

        String entry1stUUID = UUID.randomUUID().toString();
        String product1stUUID = UUID.randomUUID().toString();
        String product2ndUUID = UUID.randomUUID().toString();
        Log.i("TEST", "category: " + categoryUUID + " list: " + listUUID + " product: " + product1stUUID + " entry: " + entry1stUUID);
        mDatabase.execSQL("INSERT INTO product (_id, name, unit_id) VALUES (?, ?, NULL), (?, ?, NULL)", new String[]{
                product1stUUID,
                "product1",
                product2ndUUID,
                "product2"
        });
        mDatabase.execSQL("INSERT INTO listentry (_id, product, list) " +
                "VALUES (?, ?, ?)", new String[]{entry1stUUID, product1stUUID, listUUID});

        Cursor noEntries2 = mCategoryProvider.query(MULTIPLE_LIST_ELEMENTS_WO_CAT, null, null, null,
                null);
        assertNotNull(noEntries2);
        assertEquals(0, noEntries2.getCount());

        Cursor oneEntry = mCategoryProvider.query(MULTIPLE_LIST_ELEMENTS, null, null, null, null);
        assertNotNull(oneEntry);
        assertEquals(1, oneEntry.getCount());
        oneEntry.moveToFirst();
        assertEquals(entry1stUUID, oneEntry.getString(oneEntry.getColumnIndex("_id")));
        assertEquals(product1stUUID, oneEntry.getString(oneEntry.getColumnIndex("product")));
        assertEquals(listUUID, oneEntry.getString(oneEntry.getColumnIndex("list")));

        String entry2ndUUID = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO listentry (_id, product, list) VALUES (?, ?, ?)", new String[]{
                entry2ndUUID,
                product2ndUUID,
                listUUID
        });
        Cursor twoEntries = mCategoryProvider.query(MULTIPLE_LIST_ELEMENTS, null, null, null,
                "product.name ASC");
        assertNotNull(twoEntries);
        assertEquals(2, twoEntries.getCount());
        twoEntries.moveToFirst();
        assertEquals(entry1stUUID, twoEntries.getString(twoEntries.getColumnIndex("_id")));
        assertEquals(product1stUUID, twoEntries.getString(twoEntries.getColumnIndex("product")));
        assertEquals(listUUID, twoEntries.getString(twoEntries.getColumnIndex("list")));
        twoEntries.moveToNext();
        assertEquals(entry2ndUUID, twoEntries.getString(twoEntries.getColumnIndex("_id")));
        assertEquals(product2ndUUID, twoEntries.getString(twoEntries.getColumnIndex("product")));
        assertEquals(listUUID, twoEntries.getString(twoEntries.getColumnIndex("list")));
    }

    public void testQuerySingleListEntries() {
        String categoryUUID = UUID.randomUUID().toString();
        String listUUID = UUID.randomUUID().toString();
        String entry1stUUID = UUID.randomUUID().toString();
        String entry2ndUUID = UUID.randomUUID().toString();
        String product1stUUID = UUID.randomUUID().toString();
        String product2ndUUID = UUID.randomUUID().toString();

        Uri SINGLE_LIST_ELEMENT1 = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/" + categoryUUID + "/list/" + listUUID + "/entry/" + entry1stUUID);
        Uri SINGLE_LIST_ELEMENT2 = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/" + categoryUUID + "/list/" + listUUID + "/entry/" + entry2ndUUID);
        Uri SINGLE_LIST_ELEMENT_WO_CAT = Uri.parse("content://" + InstalistProvider.AUTHORITY +
                "/category/-/list/" + listUUID + "/entry/" + entry1stUUID);

        mDatabase.execSQL("INSERT INTO category (_id, name) VALUES (?, ?)", new String[]{
                categoryUUID,
                "category"
        });
        mDatabase.execSQL("INSERT INTO list (_id, name, category) VALUES (?, ?, ?)", new String[]{
                listUUID,
                "list",
                categoryUUID
        });

        Cursor noEntry1 = mCategoryProvider.query(SINGLE_LIST_ELEMENT1, null, null, null, null);
        assertNotNull(noEntry1);
        assertEquals(0, noEntry1.getCount());

        mDatabase.execSQL("INSERT INTO product (_id, name) VALUES (?, ?), (?, ?)", new String[]{
                product1stUUID,
                "product1",
                product2ndUUID,
                "product2"
        });
        mDatabase.execSQL("INSERT INTO listentry (_id, product, list) " +
                "VALUES (?, ?, ?), (?, ?, ?)", new String[]{
                entry1stUUID,
                product1stUUID,
                listUUID,
                entry2ndUUID,
                product2ndUUID,
                listUUID
        });

        Cursor noEntry2 = mCategoryProvider.query(SINGLE_LIST_ELEMENT_WO_CAT, null, null, null,
                null);
        assertNotNull(noEntry2);
        assertEquals(0, noEntry2.getCount());

        Cursor oneEntry1 = mCategoryProvider.query(SINGLE_LIST_ELEMENT1, null, null, null, null);
        assertNotNull(oneEntry1);
        assertEquals(1, oneEntry1.getCount());
        oneEntry1.moveToFirst();
        assertEquals(entry1stUUID, oneEntry1.getString(oneEntry1.getColumnIndex("_id")));
        assertEquals(product1stUUID, oneEntry1.getString(oneEntry1.getColumnIndex("product")));
        assertEquals(listUUID, oneEntry1.getString(oneEntry1.getColumnIndex("list")));

        Cursor oneEntry2 = mCategoryProvider.query(SINGLE_LIST_ELEMENT2, null, null, null, null);
        assertNotNull(oneEntry2);
        assertEquals(1, oneEntry2.getCount());
        oneEntry2.moveToFirst();
        assertEquals(entry2ndUUID, oneEntry2.getString(oneEntry2.getColumnIndex("_id")));
        assertEquals(product2ndUUID, oneEntry2.getString(oneEntry2.getColumnIndex("product")));
        assertEquals(listUUID, oneEntry2.getString(oneEntry2.getColumnIndex("list")));
    }

    public void testGetType() {
        String typeDirPrefix = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + InstalistProvider.BASE_VENDOR;
        String typeItemPrefix = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + InstalistProvider.BASE_VENDOR;

        assertEquals(typeDirPrefix + "category",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category")));
        assertEquals(typeItemPrefix + "category",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-")));
        assertEquals(typeDirPrefix + "list",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list")));
        assertEquals(typeItemPrefix + "list",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list/-")));
        assertEquals(typeDirPrefix + "entry",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list/-/entry")));
        assertEquals(typeItemPrefix + "entry",
                mCategoryProvider.getType(Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/-/list/-/entry/-")));
    }

    public void testInsertCategory() {
        Uri categoryUri = Uri.parse("content://" + InstalistProvider.AUTHORITY + "/category");

        ContentValues category1CV = new ContentValues(1);
        category1CV.put("name", "category1");
        Uri rtndCat1Uri = mCategoryProvider.insert(categoryUri, category1CV);
        assertNotNull(rtndCat1Uri);
        Cursor createdCategory1 = mDatabase.rawQuery("SELECT _id, name FROM category", null);
        assertNotNull(createdCategory1);
        assertEquals(1, createdCategory1.getCount());
        createdCategory1.moveToFirst();
        assertEquals("category1", createdCategory1.getString(createdCategory1.getColumnIndex("name")));
        assertEquals(rtndCat1Uri.getLastPathSegment(), createdCategory1.getString(
                createdCategory1.getColumnIndex("_id")));
        createdCategory1.close();

        ContentValues category2CV = new ContentValues(0);
        Uri rtndCat2Uri = mCategoryProvider.insert(categoryUri, category2CV);
        assertNull(rtndCat2Uri);
    }

    public void testInsertList() {
        String categoryUUID = UUID.randomUUID().toString();
        Uri woCategoryUri = Uri.parse("content://" + InstalistProvider.AUTHORITY + "/category/-/list");
        Uri wCategoryUri = Uri.parse("content://" + InstalistProvider.AUTHORITY + "/category/" +
                categoryUUID + "/list");

        ContentValues listWOCatCV = new ContentValues(1);
        listWOCatCV.put("name", "list w/o category");
        Uri resultingLWOCURI = mCategoryProvider.insert(woCategoryUri, listWOCatCV);
        assertNotNull(resultingLWOCURI);
        String resultingLWOCUUID = resultingLWOCURI.getLastPathSegment();
        Cursor testCursor = mDatabase.rawQuery("SELECT _id, name, category FROM list", null);
        assertEquals(1, testCursor.getCount());
        testCursor.moveToFirst();
        assertEquals(resultingLWOCUUID, testCursor.getString(testCursor.getColumnIndex("_id")));
        assertEquals("list w/o category", testCursor.getString(testCursor.getColumnIndex("name")));
        assertNull(testCursor.getString(testCursor.getColumnIndex("category")));
        testCursor.close();

        ContentValues wrongListWCatCV = new ContentValues(1);
        wrongListWCatCV.put("name", "wrong list w category");
        assertNull(mCategoryProvider.insert(wCategoryUri, wrongListWCatCV));
        testCursor = mDatabase.rawQuery("SELECT _id, name, category FROM list", null);
        assertEquals(1, testCursor.getCount());
        testCursor.close();

        mDatabase.execSQL("INSERT INTO category (_id, name) VALUES (?, 'cat1')", new String[]{
                categoryUUID
        });
        ContentValues listWCatCV = new ContentValues(1);
        listWCatCV.put("name", "list w category");
        Uri resultingLWCUri = mCategoryProvider.insert(wCategoryUri, listWCatCV);
        assertNotNull(resultingLWCUri);
        String resultingLWCUUID = resultingLWCUri.getLastPathSegment();
        testCursor = mDatabase.rawQuery("SELECT name, category FROM list WHERE _id = ?",
                new String[]{ resultingLWCUUID });
        assertEquals(1, testCursor.getCount());
        testCursor.moveToFirst();
        assertEquals("list w category", testCursor.getString(testCursor.getColumnIndex("name")));
        assertEquals(categoryUUID, testCursor.getString(testCursor.getColumnIndex("category")));
        testCursor.close();
    }

    public void testInsertEntry() {
        String listUUID = UUID.randomUUID().toString();
        String productUUID = UUID.randomUUID().toString();
        Uri entryUri = Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI,
                "/category/-/list/" + listUUID + "/entry");

        ContentValues negativeEntryWOList = new ContentValues(1);
        negativeEntryWOList.put(ListEntry.COLUMN_PRODUCT, productUUID);
        assertNull(mCategoryProvider.insert(entryUri, negativeEntryWOList));
        Cursor testCursor = mDatabase.rawQuery("SELECT " + ListEntry.COLUMN_ID + " FROM " +
                ListEntry.TABLE_NAME, null);
        assertEquals(0, testCursor.getCount());
        testCursor.close();

        mDatabase.execSQL("INSERT INTO list (_id, name, category) VALUES (?, 'list w/o category', null)",
                new String[]{listUUID});
        ContentValues negativeEntryWOProduct = new ContentValues(1);
        negativeEntryWOProduct.put(ListEntry.COLUMN_PRODUCT, productUUID);
        assertNull(mCategoryProvider.insert(entryUri, negativeEntryWOProduct));
        testCursor = mDatabase.rawQuery("SELECT " + ListEntry.COLUMN_ID + " FROM " +
                ListEntry.TABLE_NAME, null);
        assertEquals(0, testCursor.getCount());
        testCursor.close();

        mDatabase.execSQL("INSERT INTO " + Product.TABLE_NAME + " (" + Product.COLUMN_ID + ", " +
                Product.COLUMN_NAME + ") VALUES (?, 'product 1')", new String[]{productUUID});

        ContentValues entryMinimumCV = new ContentValues(1);
        entryMinimumCV.put(ListEntry.COLUMN_PRODUCT, productUUID);
        Uri resultingMinimumEntryURI = mCategoryProvider.insert(entryUri, entryMinimumCV);
        assertNotNull(resultingMinimumEntryURI);
        String resultingMinimumEntryUUID = resultingMinimumEntryURI.getLastPathSegment();
        testCursor = mDatabase.rawQuery("SELECT " + ListEntry.COLUMN_ID + ", " +
                ListEntry.COLUMN_PRODUCT + ", "+ ListEntry.COLUMN_LIST + " FROM " +
                ListEntry.TABLE_NAME, null);
        assertEquals(1, testCursor.getCount());
        testCursor.moveToFirst();
        assertEquals(resultingMinimumEntryUUID, testCursor.getString(testCursor.getColumnIndex(
                ListEntry.COLUMN_ID)));
        assertEquals(listUUID, testCursor.getString(testCursor.getColumnIndex(ListEntry.COLUMN_LIST)));
        assertEquals(productUUID, testCursor.getString(testCursor.getColumnIndex(
                ListEntry.COLUMN_PRODUCT)));
        testCursor.close();
    }
}