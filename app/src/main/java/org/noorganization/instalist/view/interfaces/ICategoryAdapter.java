package org.noorganization.instalist.view.interfaces;

import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

/**
 * Guarantees Access to the underlying adapter to modify the content of these adapters.
 * Created by tinos_000 on 16.06.2015.
 */
public interface ICategoryAdapter {

    /**
     * Adds the given category that should be shown in the overview.
     * @param _Category the category that should be added.
     */
    void addCategory(Category _Category);

    /**
     * Updates the given Category in the adapter.
     * @param _Category the Category to update.
     */
    void updateCategory(Category _Category);

    /**
     * Removes the given Category from the overview.
     * @param _Category the category that should be removed.
     */
    void removeCategory(Category _Category);
}
