package org.noorganization.instalist.view;

import android.os.Handler;
import android.os.Message;

/**
 * Asynchronous interfaces for Controller when Model gets updated. Since this has to happen via a
 * {@link android.os.Handler}, this class uses int's for representation of different actions and
 * is modeled as fully abstract class.
 * Created by daMihe on 20.05.2015.
 */
@Deprecated
public abstract class IChangeHandler extends Handler {

    /**
     * ITEM_ADDED_TO_LIST should be set as Message's what when a item gets added to list. The
     * message then also contains the newly created and saved
     * {@link org.noorganization.instalist.model.ListEntry}.
     */
    public static final int ITEM_ADDED_TO_LIST = 1;

    /**
     * ITEM_UPDATED should be set as Message's what when a item gets updated in a list. Updated
     * means it's amount was changed or the item was (un-)struck. The message then also contains the
     * updated and saved {@link org.noorganization.instalist.model.ListEntry}.
     */
    public static final int ITEM_UPDATED = 2;

    /**
     * ITEM_DELETED should be set as Message's what when a item gets deleted from a list. The
     * message then also contains the deleted {@link org.noorganization.instalist.model.ListEntry}
     * which should not be saved (this would recover it).
     */
    public static final int ITEM_DELETED = 3;

    /**
     * LISTS_CHANGED should be set as Message's what when a ShoppingList itself gets modified, a new
     * one gets created or if one gets deleted. The object won't be set as retrieving all lists is
     * not as expensive as retrieving and analyzing the items.
     */
    public static final int LISTS_CHANGED = 4;

    @Override
    public abstract void handleMessage(Message _message);
}
