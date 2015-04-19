package org.noorganization.instalist.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Represents a shoppinglist itself as a logical object. This object does not contain a java list.
 * Created by michi on 14.04.15.
 */
public class ShoppingList extends SugarRecord<ShoppingList> {

    public String mName;

    public ShoppingList() {
        mName = "";
    }

    public ShoppingList(String _name) {
        mName = _name;
    }

    public List<ListEntry> getEntries() {
        return Select.from(ListEntry.class).where(Condition.prop("m_list").eq(getId())).list();
    }
}
