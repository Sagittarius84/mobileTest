package org.noorganization.instalist.view;

import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;

/**
 * Created by daMihe on 18.05.2015.
 */
public class ChangeHandler extends IChangeHandler {

    private ShoppingListOverviewFragment mCurrentFragment;

    @Override
    public void handleMessage(Message _message) {
        if(mCurrentFragment == null) {
            return;
        }

            switch (_message.what) {
            case ITEM_ADDED_TO_LIST:
                Log.i("Handler", "Added Entry: " + _message.obj.toString());
                mCurrentFragment.onListItemAdded((ListEntry) _message.obj);
                break;
            case ITEM_UPDATED:
                Log.i("Handler", "Updated Entry: " + _message.obj.toString());
                    mCurrentFragment.onListItemUpdated((ListEntry)_message.obj);
                break;
            case ITEM_DELETED:
                Log.i("Handler", "Deleted Entry: " + _message.obj.toString());
                    mCurrentFragment.onListItemDeleted((ListEntry)_message.obj);
                break;
            case LISTS_CHANGED:
                Log.i("Handler", "Changed a list.");
                    mCurrentFragment.onShoppingListItemChanged((ShoppingList) _message.obj);
                break;
            default:
                Log.e("Handler", "Action for what = " + _message.what + " unknown.");
        }
    }

    public void setCurrentFragment(ShoppingListOverviewFragment _currentFragment){
        mCurrentFragment = _currentFragment;
    }
}
