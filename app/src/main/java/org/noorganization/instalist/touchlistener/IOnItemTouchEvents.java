package org.noorganization.instalist.touchlistener;

import android.view.View;

import org.noorganization.instalist.model.ListEntry;

/**
 * Created by TS on 11.05.2015.
 */
public interface IOnItemTouchEvents {


    /**
     * Event fired when swiped right over an element.
     * @param view the affected view.
     */
    public void onSwipeRight(View view);

    /**
     * Event fired when swiped left over an element.
     * @param view the affected view.
     */
    public void onSwipeLeft(View view);

    /**
     * Event fired when tapped on an element.
     * @param view the affected view.
     */
    public void onSingleTap(View view);

}
