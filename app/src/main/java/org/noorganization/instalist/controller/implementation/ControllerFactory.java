package org.noorganization.instalist.controller.implementation;

import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.controller.ITagController;
import org.noorganization.instalist.controller.IUnitController;
import org.noorganization.instalist.view.ChangeHandler;

/**
 * Holds instances of IXController (insert something for X), which themself are singletons. This
 * class makes it harder to use the wrong controller class and the module itself gets more portable,
 * since the getInstance-Methods don't have to be public and the tested interfaces have to be used.
 * Created by Michi on 11.05.2015.
 */
public class ControllerFactory {
    public static IListController getListController() {
        return ListController.getInstance();
    }

    public static IProductController getProductController() {
        return ProductController.getInstance();
    }

    public static IRecipeController getRecipeController() {
        return RecipeController.getInstance();
    }

    public static IUnitController getUnitController() {
        return UnitController.getInstance();
    }

    public static ITagController getTagController() {
        return TagController.getInstance();
    }
}
