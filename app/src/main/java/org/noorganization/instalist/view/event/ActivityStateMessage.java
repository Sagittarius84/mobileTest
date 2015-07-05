package org.noorganization.instalist.view.event;

import android.app.Activity;

/**
 * Created by daMihe on 05.07.2015.
 */
public class ActivityStateMessage {
    public enum State {
        PAUSED,
        RESUMED
    }

    public Activity mActivity;
    public State    mState;

    public ActivityStateMessage(Activity _activity, State _state) {
        mActivity = _activity;
        mState    = _state;
    }
}
