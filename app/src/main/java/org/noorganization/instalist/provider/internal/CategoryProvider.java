package org.noorganization.instalist.provider.internal;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.noorganization.instalist.provider.InstalistProvider;

/**
 * TODO: implement and describe.
 * Created by damihe on 21.10.15.
 */
public class CategoryProvider implements IInternalProvider {

    private SQLiteDatabase mDatabase;
    private UriMatcher     mMatcher;

    private static final int MULTIPLE_CATEGORIES = 1;
    private static final int SINGLE_CATEGORY = 2;

    @Override
    public void onCreate(SQLiteDatabase _db) {
        mDatabase = _db;
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category", MULTIPLE_CATEGORIES);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category/*", SINGLE_CATEGORY);
    }

    @Override
    public Cursor query(@NonNull Uri _uri, String[] _projection, String _selection, String[] _selectionArgs, String _sortOrder) {
        switch (mMatcher.match(_uri)) {
            case MULTIPLE_CATEGORIES:
                return mDatabase.query("category", _projection, _selection, _selectionArgs, null, null, _sortOrder);
            case SINGLE_CATEGORY:
            {
                String selection = "_id = ?" + (_selection != null ? " AND (" + _selection + ")" : "");
                String[] selectionArgs = new String[1 + (_selectionArgs == null ? 0 : _selectionArgs.length)];
                selectionArgs[0] = _uri.getLastPathSegment();
                if (_selectionArgs != null) {
                    System.arraycopy(_selectionArgs, 0, selectionArgs, 1, _selectionArgs.length);
                }
                return mDatabase.query("category", _projection, selection, selectionArgs, null, null, _sortOrder);
            }
            default:
                return null;
        }
    }

    @Override
    public String getType(@NonNull Uri _uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri _uri, ContentValues _values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri _uri, String _selection, String[] _selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri _uri, ContentValues _values, String _selection, String[] _selectionArgs) {
        return 0;
    }
}
