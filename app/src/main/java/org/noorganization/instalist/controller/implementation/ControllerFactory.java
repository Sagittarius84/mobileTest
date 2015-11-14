package org.noorganization.instalist.controller.implementation;

import android.content.Context;

import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.controller.ITagController;
import org.noorganization.instalist.controller.IUnitController;

/**
 * Holds instances of IXController (insert something for X), which themself are singletons. This
 * class makes it harder to use the wrong controller class and the module itself gets more portable,
 * since the getInstance-Methods don't have to be public and the tested interfaces have to be used.
 * Created by Michi on 11.05.2015.
 */
public class ControllerFactory {
    public static IListController getListController(Context _context) {
        return ListController.getInstance(_context);
    }

    public static IProductController getProductController(Context _context) {
        return ProductController.getInstance(_context);
    }

    public static IRecipeController getRecipeController(Context _context) {
        return RecipeController.getInstance(_context);
    }

    public static IUnitController getUnitController(Context _context) {
        return UnitController.getInstance(_context);
    }

    public static ITagController getTagController(Context _context) {
        return TagController.getInstance(_context);
    }

    public static ICategoryController getCategoryController(Context _context) {
        return CategoryController.getInstance(_context);
    }
}
