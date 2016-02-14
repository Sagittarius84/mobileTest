package org.noorganization.instalist.presenter.implementation;

import android.content.Context;

import org.noorganization.instalist.presenter.IPluginController;

/**
 * Factory that gas access to plugin related class instances.
 * Created by Desnoo on 14.02.2016.
 */
public class PluginControllerFactory {
    public static IPluginController getPluginController(Context _context) {
        return PluginController.getInstance(_context);
    }
}
