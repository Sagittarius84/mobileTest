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
}
