package org.noorganization.instalist.provider.internal;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by Tino on 26.10.2015.
 */
public class TagProvider implements IInternalProvider {
    //region private attributes
    private SQLiteDatabase mDatabase;
    private UriMatcher mMatcher;
    private Context mContext;

    @Override
    public void onCreate(SQLiteDatabase _db) {
        mDatabase = _db;
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // TODO impl
    }

    @Override
    public Cursor query(@NonNull Uri _uri, String[] _projection, String _selection, String[] _selectionArgs, String _sortOrder) {
        return null;
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
