package org.noorganization.instalist.touchlistener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by TS on 20.04.2015.
 * Holds an simple swipe listener.
 * TODO: some animations!
 */
public class OnSwipeListener implements View.OnTouchListener {

    private final GestureDetector mGestureDetector;
    public View mView;

    public OnSwipeListener(Context context, View view){
        mGestureDetector = new GestureDetector(context, new GestureListener());
        mView = view;
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 20;



        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

           // boolean result = false;
            float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            return false;
        }
    }
    public void onSwipeRight() {}
    public void onSwipeLeft() {}
}
