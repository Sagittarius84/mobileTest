package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.model.Category;

public class CategoryController implements ICategoryController {
    private static CategoryController mInstance;

    private CategoryController() {
    }

    static CategoryController getInstance() {
        if (mInstance == null) {
            mInstance = new CategoryController();
        }
        return mInstance;
    }

    @Override
    public Category createCategory(String _name) {
        return null;
    }

    @Override
    public Category renameCategory(Category _toRename, String _newName) {
        return null;
    }

    @Override
    public void removeCategory(Category _toRemove) {

    }
}
