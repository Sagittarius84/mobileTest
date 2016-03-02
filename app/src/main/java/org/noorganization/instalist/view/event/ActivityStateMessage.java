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
