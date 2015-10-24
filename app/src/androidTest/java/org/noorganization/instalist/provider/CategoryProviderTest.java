package org.noorganization.instalist.provider;

import android.content.ContentProvider;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.provider.internal.CategoryProvider;
import org.noorganization.instalist.provider.internal.IInternalProvider;

import java.util.UUID;

/**
 * Created by damihe on 24.10.15.
 */
public class CategoryProviderTest extends AndroidTestCase {

    IInternalProvider mCategoryProvider;
    SQLiteDatabase    mDatabase;

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
        mDatabase.execSQL("INSERT INTO category (_id, name) VALUES (?, ?)", new String[] {
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
}
