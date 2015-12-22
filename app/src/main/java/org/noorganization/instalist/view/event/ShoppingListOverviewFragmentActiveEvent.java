package org.noorganization.instalist.view.event;

/**
 * Indicates if the current fragment is active.
 * Created by Lunero on 22.12.2015.
 */
public class ShoppingListOverviewFragmentActiveEvent {

    public boolean mActive;

    public ShoppingListOverviewFragmentActiveEvent(boolean _active){
        mActive = _active;
    }
}
