package org.noorganization.instalist.view;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.noorganization.instalist.model.ListEntry;

/**
 * Created by daMihe on 18.05.2015.
 */
public class MessageHandler extends Handler {

    public static int LIST_ENTRY_ADDED = 1;

    public MessageHandler() {
        super();
    }

    @Override
    public void handleMessage(Message _message) {
        if (_message.what == LIST_ENTRY_ADDED) {
            Log.i("Handler", "Added Entry: " + _message.obj.toString());
        }
    }
}
