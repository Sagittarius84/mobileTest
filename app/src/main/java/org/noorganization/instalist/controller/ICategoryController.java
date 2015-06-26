package org.noorganization.instalist.controller;

import org.noorganization.instalist.model.Category;

/**
 * The controller for creating and changing lists categories. Use this interface to save integrity.
 * Created by daMihe on 25.05.2015.
 */
public interface ICategoryController {
    /**
     * Creates a category.
     * @param _name The name of the new category, must not be already used. Not null or empty.
     * @return The new category if creating worked, else null.
     */
    Category createCategory(String _name);

    /**
     * Renames a category.
     * @param _toRename The saved Category to change (created by {@link #createCategory(String)}.
     *                  Not null.
     * @param _newName The new name of the category, not null, empty or already used by another
     *                 Category.
     * @return The changed category if everything was OK. In other cases either the old category or
     * null if category to change was not found.
     */
    Category renameCategory(Category _toRename, String _newName);

    void removeCategory(Category _toRemove);
}
