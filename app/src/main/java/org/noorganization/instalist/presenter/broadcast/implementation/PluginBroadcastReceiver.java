/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.presenter.broadcast.implementation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import org.noorganization.instalist.presenter.PluginControllerActions;
import org.noorganization.instalist.presenter.broadcast.IPluginBroadCast;
import org.noorganization.instalist.presenter.event.PluginFoundMessage;

import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import de.greenrobot.event.EventBus;

/**
 * Broadcast Receiver for the Plugin infrastructure.
 * Created by tinos_000 on 07.01.2016.
 */
public class PluginBroadcastReceiver extends BroadcastReceiver implements IPluginBroadCast {

    static final String PLUGIN_INFO_KEY_NAME = "name";
    static final String PLUGIN_INFO_KEY_SETTINGS_ACTIVITY = "settings";
    static final String PLUGIN_INFO_KEY_MAIN_ACTIVITY = "main";
    static final String PLUGIN_INFO_KEY_PACKAGE = "package";

    private Context mContext;

    /**
     * Empty construtor for receive.
     */
    public PluginBroadcastReceiver() {
        super();
    }

    public PluginBroadcastReceiver(Context _context) {
        super();
        mContext = _context;
    }

    @Override
    public void searchPlugins() {
        Log.d("PluginBroadcastReceiver", "Sending " + PluginControllerActions.ACTION_PING);
        Intent pingBroadcast = new Intent(PluginControllerActions.ACTION_PING);
        pingBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        pingBroadcast.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.sendBroadcast(pingBroadcast);
        Log.d(getClass().getCanonicalName(), "Sent searchPlugins broadcast.");
    }

    @Override
    public void onReceive(Context _context, Intent _intent) {
        Log.d(getClass().getCanonicalName(), "Got Intent.");

        if (!_intent.getAction().equals(PluginControllerActions.ACTION_PONG)) {
            Log.d(getClass().getCanonicalName(), "No Pong action.");
            return;
        }

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
                // load classes from other apks
                String sourceDir = _context.getPackageManager().getApplicationInfo(pluginInfo.getString(PLUGIN_INFO_KEY_PACKAGE), PackageManager.GET_ACTIVITIES).sourceDir;
                PathClassLoader pathClassLoader = new dalvik.system.PathClassLoader(sourceDir, ClassLoader.getSystemClassLoader());
                event.mSettingsActivity = Class.forName(pluginInfo.getString(PLUGIN_INFO_KEY_SETTINGS_ACTIVITY), true, pathClassLoader);
            } catch (Exception e) {
                Log.e(getClass().getCanonicalName(), "Plugin loading failed: " + e.getMessage());
                return;
            }
        }
        Log.d(getClass().getCanonicalName(), "Found Plugin! " + event.mName + " in " + event.mPackage);
        // TODO: implement translator to event for bus.

        EventBus.getDefault().post(event);
    }
}
