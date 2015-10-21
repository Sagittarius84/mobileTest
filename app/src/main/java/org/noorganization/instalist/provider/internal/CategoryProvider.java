package org.noorganization.instalist.provider.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * TODO: implement and describe.
 * Created by damihe on 21.10.15.
 */
public class CategoryProvider implements IInternalProvider {
    @Override
    public void onCreate(SQLiteDatabase _db) {

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
