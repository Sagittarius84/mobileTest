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
