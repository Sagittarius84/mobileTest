package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
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
