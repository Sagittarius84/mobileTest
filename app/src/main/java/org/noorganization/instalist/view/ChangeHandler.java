package org.noorganization.instalist.view;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.noorganization.instalist.model.ListEntry;

/**
 * Created by daMihe on 18.05.2015.
 */
public class ChangeHandler extends IChangeHandler {

    @Override
    public void handleMessage(Message _message) {
        switch (_message.what) {
            case ITEM_ADDED_TO_LIST:
                Log.i("Handler", "Added Entry: " + _message.obj.toString());
                break;
            case ITEM_UPDATED:
                Log.i("Handler", "Updated Entry: " + _message.obj.toString());
                break;
            case ITEM_DELETED:
                Log.i("Handler", "Deleted Entry: " + _message.obj.toString());
                break;
            case LISTS_CHANGED:
                Log.i("Handler", "Changed a list.");
                break;
            default:
                Log.e("Handler", "Action for what = " + _message.what + " unknown.");
        }
    }
}
