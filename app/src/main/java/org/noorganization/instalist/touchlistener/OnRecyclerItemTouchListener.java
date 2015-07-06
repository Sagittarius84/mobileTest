package org.noorganization.instalist.touchlistener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by TS on 04.05.2015.
 * Touchlistener that proceed gesturedetection of swiping and tapping.
 */
public class OnRecyclerItemTouchListener implements RecyclerView.OnItemTouchListener, IOnRecyclerItemTouchEvents{

    public View mView;
    private final GestureDetector mGestureDetector;

    public OnRecyclerItemTouchListener(Context context, RecyclerView recyclerView){
        mGestureDetector = new GestureDetector(context, new GestureListener(this, recyclerView));
        mView   = recyclerView;

    }
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // maybe here are some performance tweaks possible
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    /**
     * Event fires if user flings from left to right.
     * @param childView the view of the affected action.
     * @param position the position in the recycleradapter.
     */
    public void onSwipeRight(View childView, int position) {}

    /**
     * Event fires if user flings from right to left.
     * @param childView the view of the affected action.
     * @param position the position in the recycleradapter.
     */
    public void onSwipeLeft(View childView, int position) {}
    /**
     * Event fires if user do a single tap on a item in the list.
     * @param childView the view of the affected action.
     * @param position the position in the recycleradapter.
     */
    public void onSingleTap(View childView, int position){}
    /**
     * Event fires if user do a LongPress on a item in the view..
     * @param _ChildView the view of the affected action.
     * @param _Position the position in the recycleradapter.
     */
    public void onLongPress(View _ChildView, int _Position){}



    private static final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 80;
        private static final int SWIPE_VELOCITY_THRESHOLD = 20;

        private OnRecyclerItemTouchListener mGestureListener;
        private RecyclerView mRecyclerView;

        /**
         * Creates a new instance of a GestureListener.
         * @param _OnGestureListener The listener, that listens to those events.
         * @param recyclerView the recyclerview attached to layout.
         */
        public GestureListener(OnRecyclerItemTouchListener _OnGestureListener, RecyclerView recyclerView){
            this.mGestureListener   = _OnGestureListener;
            this.mRecyclerView      = recyclerView;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView  = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            int position    = mRecyclerView.getChildAdapterPosition(childView);

            mGestureListener.onSingleTap(childView, position);
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            View childView  = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            int position    = mRecyclerView.getChildAdapterPosition(childView);

            mGestureListener.onLongPress(childView, position);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // boolean result = false;
            if(e1 == null || e2 == null) return false;

            float diffX = e2.getX() - e1.getX();

            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                View childView  = mRecyclerView.findChildViewUnder(e1.getX(), e1.getY());
                int position    = mRecyclerView.getChildAdapterPosition(childView);
                if(position < 0){
                    return false;
                }

                if (diffX > 0) {
                    mGestureListener.onSwipeRight(childView, position);
                } else {
                    mGestureListener.onSwipeLeft(childView, position);
                }
            }
            return false;
        }
    }
}