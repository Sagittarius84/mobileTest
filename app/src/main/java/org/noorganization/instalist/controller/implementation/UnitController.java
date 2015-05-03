package org.noorganization.instalist.controller.implementation;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Unit;

public class UnitController implements IUnitController {
    private static UnitController mInstance;

    public static UnitController getInstance() {
        if (mInstance == null) {
            mInstance = new UnitController();
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

        return newUnit;
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

        return toChange;
    }

    @Override
    public boolean deleteUnit(Unit _unit, int _mode) {
        if (_unit == null) {
            return true;
        }

        switch (_mode) {
            case MODE_DELETE_REFERENCES:
                SugarRecord.deleteAll(Product.class, "m_unit = ?", _unit.getId() + "");
                break;
            case MODE_UNLINK_REFERENCES:
                for (Product _toUnlink : Select.from(Product.class).
                        where(Condition.prop("m_unit").eq(_unit.getId())).list()) {
                    _toUnlink.mUnit = null;
                    _toUnlink.save();
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
        return true;
    }

    private UnitController() {
    }
}
