package org.noorganization.instalist.presenter.implementation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.noorganization.instalist.presenter.IPluginController;

/**
 * Created by damihe on 06.01.16.
 */
public class PluginController extends BroadcastReceiver implements IPluginController {

    public static final String ACTION_PING = "org.noorganization.instalist.action.PING_PLUGIN";
    public static final String ACTION_PONG = "org.noorganization.instalist.action.PONG_PLUGIN";

    private Context mContext;
    private static PluginController sInstance;

    @Override
    public void searchPlugins() {
        Log.d("BroadcastSender", "Sending " + ACTION_PING);
        Intent pingBroadcast = new Intent(ACTION_PING);
        pingBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        mContext.sendBroadcast(pingBroadcast);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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
