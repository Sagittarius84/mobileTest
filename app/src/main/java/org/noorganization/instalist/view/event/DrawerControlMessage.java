package org.noorganization.instalist.view.event;

/**
 * Message to control the drawer interaction.
 * Created by Lunero on 23.12.2015.
 */
public class DrawerControlMessage {

    public boolean mOpenDrawer;

    public DrawerControlMessage(boolean _openDrawer) {
        this.mOpenDrawer = _openDrawer;
    }
}
