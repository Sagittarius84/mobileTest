package org.noorganization.instalist.controller;

import org.noorganization.instalist.model.Unit;

/**
 * The interface for modifying Units (created by software engineering). From view part, do only
 * modify data over this interface for keeping integrity.
 * Created by michi on 03.05.2015.
 */
public interface IUnitController {
    public enum DeletionMode {
        BREAK_DELETION,
        UNLINK_REFERENCES,
        DELETE_REFERENCES
    }

    /**
     * Creates a Unit with given name or fails, if it already exists.
     * @param _name The name of the new Unit. Not null.
     * @return The new Unit or null, if the parameter is invalid or a Unit with that name already
     * exists.
     */
    public Unit createUnit(String _name);

    /**
     * Rename the Unit, if possible.
     * @param _unit The valid unit to rename. Not null.
     * @param _newName The new name of the unit. Not null. Not existent.
     * @return The modified Unit if everything went ok, the last saved unit if renaming was not
     * possible or null if the unit could not be found.
     */
    public Unit renameUnit(Unit _unit, String _newName);

    /**
     * Deletes a unit. Deletion can be controlled with the mode parameter.
     * @param _unit The valid unit to delete. Not null.
     * @param _mode Mode for handling conflicts.
     * @return False if nothing was deleted (can happen if mode is set to BREAK_DELETION), else
     * true. Also true if unit was already deleted.
     */
    public boolean deleteUnit(Unit _unit, DeletionMode _mode);


}
