package org.noorganization.instalist.provider.internal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.provider.InstalistProvider;
import org.noorganization.instalist.utils.SQLiteUtils;
import org.noorganization.instalist.view.utils.ProviderUtils;

/**
 * Provider for the products used in the application.
 * Created by Tino on 24.10.2015.
 */
public class ProductProvider implements IInternalProvider {
    //region private attributes
    private SQLiteDatabase mDatabase;
    private UriMatcher mMatcher;

    private static final int SINGLE_PRODUCT = 1;
    private static final int MULTIPLE_PRODUCTS = 2;

    private static final String SINGLE_PRODUCT_STRING = "product/*";
    private static final String MULTIPLE_PRODUCTS_STRING = "product";

    //endregion private attributes

    //region public attributes
    public static final String SINGLE_PRODUCT_CONTENT_URI = InstalistProvider.BASE_CONTENT_URI + "/" + SINGLE_PRODUCT_STRING;
    public static final String MULTIPLE_PRODUCT_CONTENT_URI = InstalistProvider.BASE_CONTENT_URI + "/" + MULTIPLE_PRODUCTS_STRING;

    public static final String PRODUCT_BASE_TYPE = "vnd.noorganization.product";
    //endregion public attributes

    //region public overriden methods
    @Override
    public void onCreate(SQLiteDatabase _db) {
        mDatabase = _db;
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mMatcher.addURI(InstalistProvider.AUTHORITY, SINGLE_PRODUCT_STRING, SINGLE_PRODUCT);
        mMatcher.addURI(InstalistProvider.AUTHORITY, MULTIPLE_PRODUCTS_STRING, MULTIPLE_PRODUCTS);

    }

    @Override
    public Cursor query(@NonNull Uri _uri, String[] _projection, String _selection, String[] _selectionArgs, String _sortOrder) {
        switch (mMatcher.match(_uri)) {

            case SINGLE_PRODUCT:
                String selection = ProviderUtils.getSelectionWithIdQuery(Product.COLUMN_ID, _selection);
                String[] selectionArgs = ProviderUtils.getSelectionArgsWithId(_selectionArgs, _uri.getLastPathSegment());
                return mDatabase.query(Product.TABLE_NAME, _projection, selection, selectionArgs, null, null, _sortOrder);

            case MULTIPLE_PRODUCTS:
                return mDatabase.query(Product.TABLE_NAME, _projection, _selection, _selectionArgs, null, null, _sortOrder);

            default:
                return null;
        }
    }

    @Override
    public String getType(@NonNull Uri _uri) {
        switch (mMatcher.match(_uri)) {
            case SINGLE_PRODUCT:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PRODUCT_BASE_TYPE;
            case MULTIPLE_PRODUCTS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PRODUCT_BASE_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri _uri, ContentValues _values) {
        switch (mMatcher.match(_uri)) {
            case SINGLE_PRODUCT:
                long rowId = mDatabase.insert(Product.TABLE_NAME, null, _values);
                if (rowId == -1) {
                    throw new SQLiteException("Failed to add a record into " + _uri);
                }
                Cursor cursor = mDatabase.query(Product.TABLE_NAME, new String[]{Product.COLUMN_ID},
                        SQLiteUtils.COLUMN_ROW_ID + "=?", new String[]{String.valueOf(rowId)},
                        null, null, null, null);

                cursor.moveToFirst();
                return Uri.parse(SINGLE_PRODUCT_CONTENT_URI.replace("*",
                        cursor.getString(cursor.getColumnIndex(Product.COLUMN_ID))));

            case MULTIPLE_PRODUCTS:
                // TODO: implement!
                return Uri.parse(MULTIPLE_PRODUCT_CONTENT_URI);
            default:
                return null;
        }
    }

    // TODO: implement delete
    @Override
    public int delete(@NonNull Uri _uri, String _selection, String[] _selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri _uri, ContentValues _values, String _selection, String[] _selectionArgs) {
        return 0;
    }

    //endregion overwritten methods
}
