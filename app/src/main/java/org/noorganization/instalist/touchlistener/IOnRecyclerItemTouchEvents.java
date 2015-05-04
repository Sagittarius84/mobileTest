package org.noorganization.instalist.touchlistener;

import android.view.View;

/**
 * Created by TS on 04.05.2015.
 * Interface for usage of @link{OnRecyclerItemTouchListener}. Delivers the methods for callbacks.
 */
public interface IOnRecyclerItemTouchEvents {

    /**
     * Event fired when swiped right over an element.
     * @param view the affected view.
     * @param position the position in the recyclerview.
     */
        public void onSwipeRight(View view, int position);

    /**
     * Event fired when swiped left over an element.
     * @param view the affected view.
     * @param position the position in the recyclerview.
     */
        public void onSwipeLeft(View view, int position);
    /**
     * Event fired when tapped on an element.
     * @param view the affected view.
     * @param position the position in the recyclerview.
     */
        public void onSingleTap(View view, int position);


}
