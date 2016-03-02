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

/**
 * Event for requesting a change in activities toolbar.
 *
 * Fired by a fragment. The content of the message is something like a proposal created by a
 * fragment for the activity for changing title and drawer lock state. The corresponding activity
 * neither has to receive the request nor has to change the settings delivered with this message. It
 * should also only be processed by the active activity.
 * Created by daMihe on 05.07.2015.
 */
public class ToolbarChangeMessage {
    public Boolean mNewLockState;
    public String  mNewTitle;

    public ToolbarChangeMessage(Boolean _newLockState, String _newTitle) {
        mNewLockState = _newLockState;
        mNewTitle     = _newTitle;
    }
}
