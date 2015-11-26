package org.noorganization.instalist.view.event;

import android.app.Activity;

/**
 * Event for notifying fragments.
 *
 * Should be fired by Activities that handles fragments.
 * Created by daMihe on 05.07.2015.
 */
public class ActivityStateMessage {
    public enum State {
        PAUSED,
        RESUMED
    }

    /**
     * The activity which state changes.
     */
    public Activity mActivity;

    /**
     * The new state of the activity.
     */
    public State    mState;

    public ActivityStateMessage(Activity _activity, State _state) {
        mActivity = _activity;
        mState    = _state;
    }
}
