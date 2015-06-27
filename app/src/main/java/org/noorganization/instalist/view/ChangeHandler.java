package org.noorganization.instalist.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.interfaces.ISideDrawerListDataEvents;

/**
 * Created by daMihe on 18.05.2015.
 */
public class ChangeHandler extends IChangeHandler {

    private ShoppingListOverviewFragment mCurrentFragment;
    private ISideDrawerListDataEvents    mSideDrawerEvents;

    @Override
    public void handleMessage(Message _message) {
        if (mCurrentFragment == null || mSideDrawerEvents == null) {
           return;
        }

        switch (_message.what) {
            case ITEM_ADDED_TO_LIST:
                Log.i("Handler", "Added Entry: " + _message.obj.toString());
                mCurrentFragment.onListItemAdded((ListEntry) _message.obj);
                break;
            case ITEM_UPDATED:
                Log.i("Handler", "Updated Entry: " + _message.obj.toString());
                mCurrentFragment.onListItemUpdated((ListEntry) _message.obj);
                break;
            case ITEM_DELETED:
                Log.i("Handler", "Deleted Entry: " + _message.obj.toString());
                mCurrentFragment.onListItemDeleted((ListEntry) _message.obj);
                break;
            case LISTS_CHANGED:
                Log.i("Handler", "Changed a list.");
                mSideDrawerEvents.updateList((ShoppingList) _message.obj);
                break;
            default:
                Log.e("Handler", "Action for what = " + _message.what + " unknown.");
        }
    }

    public void setCurrentFragment(ShoppingListOverviewFragment _currentFragment) {
        mCurrentFragment = _currentFragment;
    }

    public void setCurrentBaseActivity(Activity _Activity) {
        try {
            if(_Activity != null) {
                mSideDrawerEvents = (ISideDrawerListDataEvents) _Activity;
            } else{
                mSideDrawerEvents = null;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(_Activity.toString() + " has no ISideDrawerListDataEvents"
                    + " interface implemented");
        }
    }
}
