package org.noorganization.instalist.controller.implementation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.controller.event.CategoryChangedMessage;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.provider.InstalistProvider;

import java.util.UUID;

import de.greenrobot.event.EventBus;

public class CategoryController implements ICategoryController {
    private static CategoryController mInstance;

    private EventBus mBus;
    private Context  mContext;

    private CategoryController(@NonNull Context _context) {
        mContext = _context;
        mBus = EventBus.getDefault();
    }

    static CategoryController getInstance(@NonNull Context _context) {
        if (mInstance == null) {
            mInstance = new CategoryController(_context);
        }
        return mInstance;
    }

    @Override
    public Category createCategory(String _name) {
        if (_name == null || _name.length() == 0 || nameUsed(_name, null)) {
            return null;
        }

        ContentValues newCatCV = new ContentValues(1);
        newCatCV.put(Category.COLUMN.NAME, _name);
        Uri newlyCreatedCat = mContext.getContentResolver().insert(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                newCatCV);
        if (newlyCreatedCat == null) {
            return null;
        }

        Category rtn = new Category(UUID.fromString(newlyCreatedCat.getLastPathSegment()), _name);

        mBus.post(new CategoryChangedMessage(Change.CREATED, rtn));

        return rtn;
    }

    @Override
    public Category getCategoryByUUID(@NonNull UUID _uuid) {
        Cursor resultCursor = mContext.getContentResolver().query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/"+_uuid.toString()),
                new String[]{ Category.COLUMN.NAME},
                null, null, null);
        if (resultCursor == null) {
            Log.e(getClass().getCanonicalName(), "Query result was null. Returning no result.");
            return null;
        }
        if (resultCursor.getCount() == 0) {
            resultCursor.close();
            return null;
        }

        resultCursor.moveToFirst();
        Category rtn = new Category();
        rtn.mUUID = _uuid;
        rtn.mName = resultCursor.getString(resultCursor.getColumnIndex(
                Category.COLUMN.NAME));
        resultCursor.close();
        return rtn;
    }

    @Override
    public Category renameCategory(Category _toRename, String _newName) {
        if (_toRename == null) {
            return null;
        }

        Category rtn = getCategoryByUUID(_toRename.mUUID);
        if (rtn == null) {
            return null;
        }

        if (_newName != null && _newName.length() > 0 && !nameUsed(_newName, rtn.mUUID)) {
            ContentValues updateCV = new ContentValues(1);
            updateCV.put(Category.COLUMN.NAME, _newName);
            if (mContext.getContentResolver().update(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category/" + rtn.mUUID),
                    updateCV,
                    null, null) == 1) {
                rtn.mName = _newName;
            }
        }

        mBus.post(new CategoryChangedMessage(Change.CHANGED, rtn));

        return rtn;
    }

    @Override
    public void removeCategory(Category _toRemove) {
        if (_toRemove == null) {
            return;
        }

        if (getCategoryByUUID(_toRemove.mUUID) != null) {
            // TODO Unlink or delete List previously.

            if (mContext.getContentResolver().delete(
                    Uri.withAppendedPath(
                            InstalistProvider.BASE_CONTENT_URI,
                            "category/"+_toRemove.mUUID.toString()),
                    null,
                    null) == 1) {

                mBus.post(new CategoryChangedMessage(Change.DELETED, _toRemove));
            }
        }
    }

    private boolean nameUsed(String _search, UUID _ignoreId) {
        Cursor catsToCheck;
        if (_ignoreId != null) {
            catsToCheck = mContext.getContentResolver().query(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                    null,
                    Category.COLUMN.ID + " != ? AND " +
                            Category.COLUMN.NAME + " = ?",
                    new String[]{
                            _ignoreId.toString(),
                            _search
                    },
                    null);
        } else {
            catsToCheck = mContext.getContentResolver().query(
                    Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "category"),
                    null,
                    Category.COLUMN.NAME + " = ?",
                    new String[]{ _search },
                    null);
        }
        if (catsToCheck == null) {
            Log.e(getClass().getCanonicalName(), "Query for searching name duplicates nulled. " +
                    "Returning true (= there is a duplicate).");
            return true;
        }
        int count = catsToCheck.getCount();
        catsToCheck.close();

        return (count != 0);
    }
}
