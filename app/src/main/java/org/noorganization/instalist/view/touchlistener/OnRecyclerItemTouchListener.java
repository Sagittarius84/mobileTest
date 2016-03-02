/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.touchlistener;

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
        private static final float SWIPE_MAX_ANGLE = 3.1415f / 5;

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

                float diffY = e2.getY() - e1.getY();
                double normY = diffY / Math.sqrt(diffX * diffX + diffY * diffY);
                double absMaxNormY = Math.sin((SWIPE_MAX_ANGLE / 2.0f));

                if (normY <= absMaxNormY && normY >= -absMaxNormY) {
                    if (diffX > 0) {
                        mGestureListener.onSwipeRight(childView, position);
                    } else {
                        mGestureListener.onSwipeLeft(childView, position);
                    }
                }
            }
            return false;
        }
    }
}