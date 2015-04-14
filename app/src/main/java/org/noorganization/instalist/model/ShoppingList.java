package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a shoppinglist itself as a logical object. This object does not contain a java list.
 * Created by michi on 14.04.15.
 */
public class ShoppingList extends SugarRecord<ShoppingList> {

    String mName;

    public ShoppingList() {
        mName = "";
    }

    public ShoppingList(String _name) {
        mName = _name;
    }
}
