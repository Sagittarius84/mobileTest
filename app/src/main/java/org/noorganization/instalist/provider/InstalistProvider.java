package org.noorganization.instalist.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.noorganization.instalist.provider.internal.CategoryProvider;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.TagProvider;
import org.noorganization.instalist.provider.internal.UnitProvider;

import java.util.HashMap;
import java.util.List;

/**
 * Created by damihe on 19.10.15.
 */
public class InstalistProvider extends ContentProvider {

    public final static String AUTHORITY = "org.noorganization.instalist.provider";

    public final static String BASE_VENDOR = "vnd.org.noorganization.instalist.";
    /**
     * The base content uri. Build a uri with the table paths.
     **/
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // ACM_XXX = Authority Code Multiple Lines; ACS_XXX = Authority Code Single Line
    // as described in http://developer.android.com/guide/topics/providers/content-provider-creating.html
//    private static final int ACM_CATEGORY = 1;
//    private static final int ACS_CATEGORY = 2;
//    private static final int ACM_LIST = 3;
//    private static final int ACS_LIST = 4;
//    private static final int ACM_LISTENTRY = 5;
//    private static final int ACS_LISTENTRY = 6;
//    private static final int ACM_PRODUCT = 7;
//    private static final int ACS_PRODUCT = 8;
//    private static final int ACM_TAG = 9;
//    private static final int ACS_TAG = 10;
//    private static final int ACM_TAGGEDPRODUCT = 11;
//    private static final int ACS_TAGGEDPRODUCT = 12;
//    private static final int ACM_UNIT = 13;
//    private static final int ACS_UNIT = 14;
//    private static final int ACM_RECIPE = 15;
//    private static final int ACS_RECIPE = 16;
//    private static final int ACM_INGREDIENT = 17;
//    private static final int ACS_INGREDIENT = 18;

//    private UriMatcher mMatcher;

    private SQLiteDatabase mDatabase;
    private HashMap<String, IInternalProvider> mInternalProviders;

    @Override
    public boolean onCreate() {

//        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        mMatcher.addURI(AUTHORITY, "category", ACM_CATEGORY);
//        mMatcher.addURI(AUTHORITY, "category/*", ACS_CATEGORY);
//        mMatcher.addURI(AUTHORITY, "category/*/list", ACM_LIST);
//        mMatcher.addURI(AUTHORITY, "category/*/list/*", ACS_LIST);
//        mMatcher.addURI(AUTHORITY, "category/*/list/*/entry", ACM_LISTENTRY);
//        mMatcher.addURI(AUTHORITY, "category/*/list/*/entry/*", ACM_LISTENTRY);
//        mMatcher.addURI(AUTHORITY, "product", ACM_PRODUCT);
//        mMatcher.addURI(AUTHORITY, "product/*", ACS_PRODUCT);
//        mMatcher.addURI(AUTHORITY, "product/*/tag", ACM_TAGGEDPRODUCT);
//        mMatcher.addURI(AUTHORITY, "product/*/tag/*", ACS_TAGGEDPRODUCT);
//        mMatcher.addURI(AUTHORITY, "tag", ACM_TAG);
//        mMatcher.addURI(AUTHORITY, "tag/*", ACM_TAG);
//        mMatcher.addURI(AUTHORITY, "tag/*/product", ACM_TAG);
//        mMatcher.addURI(AUTHORITY, "unit", ACM_UNIT);
//        mMatcher.addURI(AUTHORITY, "unit/*", ACM_UNIT);
//        mMatcher.addURI(AUTHORITY, "recipe", ACM_RECIPE);
//        mMatcher.addURI(AUTHORITY, "recipe/*", ACS_RECIPE);
//        mMatcher.addURI(AUTHORITY, "recipe/*/ingredient", ACM_INGREDIENT);
//        mMatcher.addURI(AUTHORITY, "recipe/*/ingredient/*", ACS_INGREDIENT);

        // TODO: Use persistent db.
        mDatabase = new DBOpenHelper(getContext(), ":memory:").getWritableDatabase();

        IInternalProvider categoryProvider = new CategoryProvider();
        IInternalProvider productProvider = new ProductProvider(getContext());
        IInternalProvider unitProvider = new UnitProvider(getContext());
        IInternalProvider tagProvider = new TagProvider(getContext());

        categoryProvider.onCreate(mDatabase);
        productProvider.onCreate(mDatabase);
        unitProvider.onCreate(mDatabase);
        tagProvider.onCreate(mDatabase);

        mInternalProviders = new HashMap<>();
        mInternalProviders.put("category", categoryProvider);
        mInternalProviders.put("prodcut", productProvider);
        mInternalProviders.put("unit", unitProvider);
        mInternalProviders.put("tag", tagProvider);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri _uri, String[] _projection, String _selection,
                        String[] _selectionArgs, String _sortOrder) {
        IInternalProvider provider = getInternalProvider(_uri);
        if (provider != null) {
            return provider.query(_uri, _projection, _selection, _selectionArgs, _sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri _uri) {
        IInternalProvider provider = getInternalProvider(_uri);
        if (provider != null) {
            return provider.getType(_uri);
        }
        return null;
    }

    /**
     * Returns the right provider for the given uri. The decision is made by the first path segment.
     *
     * @param _uri The uri to provide content for.
     * @return Either the internal provider or null if uri is not okay or it's the wrong authority.
     */
    private IInternalProvider getInternalProvider(@NonNull Uri _uri) {
        if (!AUTHORITY.equals(_uri.getAuthority())) {
            return null;
        }

        List<String> pathSegments = _uri.getPathSegments();
        if (pathSegments.size() == 0) {
            return null;
        }

        String firstPart = pathSegments.get(0);
        if (mInternalProviders.containsKey(firstPart)) {
            return mInternalProviders.get(firstPart);
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri _uri, ContentValues _values) {
        IInternalProvider provider = getInternalProvider(_uri);
        if (provider != null) {
            Uri rtnUri = provider.insert(_uri, _values);
            if (rtnUri != null) {
                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(rtnUri, null);
                }
            }
            return rtnUri;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri _uri, String _selection, String[] _selectionArgs) {
        IInternalProvider provider = getInternalProvider(_uri);
        if (provider != null) {
            return provider.delete(_uri, _selection, _selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri _uri, ContentValues _values, String _selection,
                      String[] _selectionArgs) {
        IInternalProvider provider = getInternalProvider(_uri);
        if (provider != null) {
            return provider.update(_uri, _values, _selection, _selectionArgs);
        }
        return 0;
    }
}
