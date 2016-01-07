package org.noorganization.instalist.presenter.implementation;

import android.content.Context;

import org.noorganization.instalist.presenter.IPluginController;
import org.noorganization.instalist.presenter.broadcast.IPluginBroadCast;
import org.noorganization.instalist.presenter.broadcast.implementation.PluginBroadcastReceiver;

/**
 * The PluginController delivers functionality to trigger a plugin search.
 * It delivers the {@link org.noorganization.instalist.presenter.event.PluginFoundMessage} when detected.
 *
 * Type: Singleton.
 *
 * Created by damihe on 06.01.16.
 */
public class PluginController implements IPluginController {


    private static PluginController sInstance;
    private IPluginBroadCast mPluginBroadcastReceiver;

    @Override
    public void searchPlugins() {
        mPluginBroadcastReceiver.searchPlugins();
    }


    static PluginController getInstance(Context _context) {
        if (sInstance == null) {
            sInstance = new PluginController(_context);
        }
        return sInstance;
    }

    private PluginController() {
    }

    private PluginController(Context _context) {
        mPluginBroadcastReceiver = new PluginBroadcastReceiver(_context);
    }
}
