package org.noorganization.instalist.provider.internal;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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
    private static final int MULTIPLE_LISTS = 3;
    private static final int SINGLE_LIST = 4;

    @Override
    public void onCreate(SQLiteDatabase _db) {
        mDatabase = _db;
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category", MULTIPLE_CATEGORIES);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category/*", SINGLE_CATEGORY);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category/*/list", MULTIPLE_LISTS);
        mMatcher.addURI(InstalistProvider.AUTHORITY, "category/*/list/*", SINGLE_LIST);
    }

    @Override
    public Cursor query(@NonNull Uri _uri, String[] _projection, String _selection, String[] _selectionArgs, String _sortOrder) {
        switch (mMatcher.match(_uri)) {
            case MULTIPLE_CATEGORIES:
                return mDatabase.query("category", _projection, _selection, _selectionArgs, null, null, _sortOrder);
            case SINGLE_CATEGORY:
            {
                String selection = prependSelection("_id = ?", _selection);
                String[] selectionArgs = prependArgs(new String[] {_uri.getLastPathSegment()},
                        _selectionArgs);
                return mDatabase.query("category", _projection, selection, selectionArgs, null, null, _sortOrder);
            }
            case MULTIPLE_LISTS:
            {
                String category = _uri.getPathSegments().get(1);
                if (category.equals("-")) {
                    String selection = prependSelection("category IS NULL", _selection);
                    return mDatabase.query("list", _projection, selection,_selectionArgs, null,
                            null, _sortOrder);
                } else {
                    String selection = prependSelection("category = ?", _selection);
                    String[] selectionArgs = prependArgs(new String[]{ category }, _selectionArgs);
                    return mDatabase.query("list", _projection, selection, selectionArgs, null, null,
                            _sortOrder);
                }
            }
            case SINGLE_LIST:
            {
                String category = _uri.getPathSegments().get(1);
                if (category.equals("-")) {
                    String selection = prependSelection("category IS NULL AND _id = ?", _selection);
                    String[] selectionArgs = prependArgs(new String[]{_uri.getLastPathSegment()},
                            _selectionArgs);
                    return mDatabase.query("list", _projection, selection, selectionArgs, null,
                            null, _sortOrder);
                } else {
                    String selection = prependSelection("category = ? AND _id = ?", _selection);
                    String[] selectionArgs = prependArgs(new String[]{ category,
                            _uri.getLastPathSegment() }, _selectionArgs);
                    return mDatabase.query("list", _projection, selection, selectionArgs, null, null,
                            _sortOrder);
                }
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

    private static String prependSelection(String _prependedSelection, String _originalSelection) {
        return _prependedSelection + (_originalSelection == null ? "" : " AND (" + _originalSelection + ")");
    }

    private static String[] prependArgs(@NonNull String[] _prependedArgs, String[] _originalArgs) {
        String[] args = new String[_prependedArgs.length + (_originalArgs == null ? 0 : _originalArgs.length)];
        System.arraycopy(_prependedArgs, 0, args, 0, _prependedArgs.length);
        if (_originalArgs != null) {
            System.arraycopy(_originalArgs, 0, args, _prependedArgs.length, _originalArgs.length);
        }
        return args;
    }
}
