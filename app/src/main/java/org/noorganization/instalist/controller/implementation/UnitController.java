package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.IUnitController;
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
        // TODO This method is a stub.
        return null;
    }

    @Override
    public Unit renameUnit(Unit _unit, String _newName) {
        // TODO This method is a stub.
        return null;
    }

    @Override
    public boolean deleteUnit(Unit _unit, DeletionMode _mode) {
        // TODO This method is a stub.
        return false;
    }

    private UnitController() {
    }
}
