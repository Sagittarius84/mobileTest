package org.noorganization.instalist.touchlistener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.noorganization.instalist.model.ListEntry;

/**
 * Created by TS on 20.04.2015.
 * Holds an combined touch and swipe listener.
 * TODO: some animations!
 */
public class OnGestureListener implements View.OnTouchListener {
    public View mView;
    public ListEntry mEntry;
    private final GestureDetector mGestureDetector;


    public OnGestureListener(Context context, View view, ListEntry entry){
        mGestureDetector = new GestureDetector(context, new GestureListener(this));
        mView   = view;
        mEntry  = entry;
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSingleTap(){}

    private static final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 20;

        private OnGestureListener mGestureListener;

        public GestureListener(OnGestureListener _OnGestureListener){
            mGestureListener = _OnGestureListener;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mGestureListener.onSingleTap();
            return true;
        }

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
                        mGestureListener.onSwipeRight();
                    } else {
                        mGestureListener.onSwipeLeft();
                    }
                }
            return false;
        }
    }

}
