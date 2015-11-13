package org.noorganization.instalist.controller.implementation;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.controller.event.Change;
import org.noorganization.instalist.controller.event.UnitChangedMessage;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.provider.InstalistProvider;

import de.greenrobot.event.EventBus;

public class UnitController implements IUnitController {

    private static UnitController mInstance;

    private EventBus        mBus;
    private Context         mContext;
    private ContentResolver mResolver;

    private UnitController(Context _context) {
        mBus = EventBus.getDefault();
        mContext = _context;
        mResolver = mContext.getContentResolver();
    }

    static UnitController getInstance() {
        if (mInstance == null) {
            mInstance = new UnitController(Context _context);
        }
        return mInstance;
    }

    @Override
    public Unit createUnit(String _name) {
        if (_name == null ||
                Select.from(Unit.class).where(Condition.prop("m_name").eq(_name)).count() != 0) {
            return null;
        }

        Unit newUnit = new Unit(_name);
        newUnit.save();

        mBus.post(new UnitChangedMessage(Change.CREATED, newUnit));

        return newUnit;
    }

    @Override
    public Unit findById(@NonNull String _uuid) {
        Cursor unitCursor = mResolver.query(
                Uri.withAppendedPath(InstalistProvider.BASE_CONTENT_URI, "unit"),
                Unit.COLUMN.ALL_COLUMNS,
                Unit.COLUMN.ID + " = ?",
                new String[]{_uuid},
                null);
        if (unitCursor == null) {
            Log.e(getClass().getCanonicalName(), "Searching for Product by UUID failed with null " +
                    "instead of Cursor. Returning no Product.");
            return null;
        } else if (unitCursor.getCount() == 0) {
            unitCursor.close();
            return null;
        }
        unitCursor.moveToFirst();
        Unit rtn = new Unit();
        rtn.mUUID = _uuid;
        rtn.mName = unitCursor.getString(unitCursor.getColumnIndex(Unit.COLUMN.NAME));
        unitCursor.close();
        return rtn;
    }

    @Override
    public Unit renameUnit(Unit _unit, String _newName) {
        if (_unit == null) {
            return null;
        }

        Unit toChange = SugarRecord.findById(Unit.class, _unit.getId());
        if (toChange == null || _newName == null) {
            return toChange;
        }

        for (Unit toCheck : Select.from(Unit.class).where(Condition.prop("m_name").eq(_newName)).list()) {
            if (!toCheck.getId().equals(_unit.getId())) {
                return toChange;
            }
        }

        toChange.mName = _newName;
        toChange.save();

        mBus.post(new UnitChangedMessage(Change.CHANGED, toChange));

        return toChange;
    }

    @Override
    public boolean deleteUnit(Unit _unit, int _mode) {
        if (_unit == null) {
            return true;
        }

        IProductController productController = ControllerFactory.getProductController();

        switch (_mode) {
            case MODE_DELETE_REFERENCES:
                for (Product toDelete : Select.from(Product.class).
                        where(Condition.prop("m_unit").eq(_unit.getId())).list()) {
                    productController.removeProduct(toDelete, true);
                }
                break;
            case MODE_UNLINK_REFERENCES:
                for (Product toUnlink : Select.from(Product.class).
                        where(Condition.prop("m_unit").eq(_unit.getId())).list()) {
                    toUnlink.mUnit = null;
                    productController.modifyProduct(toUnlink);
                }
                break;
            case MODE_BREAK_DELETION:
                if (Select.from(Product.class).where(Condition.prop("m_unit").eq(_unit.getId())).
                        count() == 0) {
                    break;
                }
            default:
                return false;
        }

        _unit.delete();

        mBus.post(new UnitChangedMessage(Change.DELETED, _unit));

        return true;
    }
}
