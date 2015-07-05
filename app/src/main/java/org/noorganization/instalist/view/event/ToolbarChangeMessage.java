package org.noorganization.instalist.view.event;

/**
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
