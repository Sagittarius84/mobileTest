package org.noorganization.instalist.view.touchlistener;

import android.view.View;

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

    /**
     * Event fired when long tap on an element.
     * @param view the affected view.
     */
    public void onLongTap(View view);

}
