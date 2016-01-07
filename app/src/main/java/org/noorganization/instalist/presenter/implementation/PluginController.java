package org.noorganization.instalist.presenter.implementation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.noorganization.instalist.presenter.IPluginController;
import org.noorganization.instalist.presenter.event.PluginFoundMessage;

/**
 * Created by damihe on 06.01.16.
 */
public class PluginController extends BroadcastReceiver implements IPluginController {

    static final String PLUGIN_INFO_KEY_NAME = "name";
    static final String PLUGIN_INFO_KEY_SETTINGS_ACTIVITY = "settings";
    static final String PLUGIN_INFO_KEY_MAIN_ACTIVITY = "main";
    static final String PLUGIN_INFO_KEY_PACKAGE = "package";

    public static final String ACTION_PING = "org.noorganization.instalist.action.PING_PLUGIN";
    public static final String ACTION_PONG = "org.noorganization.instalist.action.PONG_PLUGIN";

    private Context mContext;
    private static PluginController sInstance;

    @Override
    public void searchPlugins() {
        Log.d("BroadcastSender", "Sending " + ACTION_PING);
        Intent pingBroadcast = new Intent(ACTION_PING);
        pingBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        pingBroadcast.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.sendBroadcast(pingBroadcast);
    }

    @Override
    public void onReceive(Context _context, Intent _intent) {
        Log.d(getClass().getCanonicalName(), "Got Intent.");
        Bundle pluginInfo = _intent.getExtras();
        PluginFoundMessage event = new PluginFoundMessage(pluginInfo.getString(PLUGIN_INFO_KEY_NAME),
                pluginInfo.getString(PLUGIN_INFO_KEY_PACKAGE));
        Log.d(getClass().getCanonicalName(), "Trying find main activity");
        if (pluginInfo.containsKey(PLUGIN_INFO_KEY_MAIN_ACTIVITY)) {
            try {
                event.mMainActivity = Class.forName(
                        pluginInfo.getString(PLUGIN_INFO_KEY_MAIN_ACTIVITY));
            } catch (Exception e) {
                Log.e(getClass().getCanonicalName(), "Plugin loading failed: " + e.getMessage());
                return;
            }
        }
        Log.d(getClass().getCanonicalName(), "Trying find settings activity");
        if (pluginInfo.containsKey(PLUGIN_INFO_KEY_SETTINGS_ACTIVITY)) {
            try {
                event.mSettingsActivity = Class.forName(
                        pluginInfo.getString(PLUGIN_INFO_KEY_SETTINGS_ACTIVITY));
            } catch (Exception e) {
                Log.e(getClass().getCanonicalName(), "Plugin loading failed: " + e.getMessage());
            }
        }
        Log.d(getClass().getCanonicalName(), "Found Plugin! " + event.mName + " in " + event.mPackage);
        // TODO: implement translator to event for bus.
    }

    static PluginController getInstance(Context _context) {
        if (sInstance == null) {
            sInstance = new PluginController();
        }
        sInstance.mContext = _context;
        return sInstance;
    }

    private PluginController(){
    }
}
