package org.noorganization.instalist.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shoppinglist itself as a logical object. This object does not contain a java list.
 * Created by michi on 14.04.15.
 */
public class ShoppingList extends SugarRecord<ShoppingList> {
    public final static String LIST_NAME_ATTR = "m_name";

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

    /**
     * Searches a ShoppingList by name. The name has to match exactly, or nothing will be found.
     * @param _name The name of the list. Any String but not null.
     * @return Either the found list or null if no list is matching.
     */
    public ShoppingList findByName(String _name) {
        if (_name == null) {
            return null;
        }

        return Select.from(ShoppingList.class).where(Condition.prop("m_name").eq(_name)).first();
    }

    /**
     * Adds all listnames to a list.
     * @return a list with the current shoppingListNames.
     */
    public static List<String> getShoppingListNames(){
        List<ShoppingList> shoppingLists = Select.from(ShoppingList.class).list();
        List<String> shoppingListNames = new ArrayList<>();

        for (ShoppingList shoppingList : shoppingLists) {
            shoppingListNames.add(shoppingList.mName);
        }

        return shoppingListNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        ShoppingList that = (ShoppingList) o;

        return (getId().equals(that.getId()) && mName.equals(that.mName));

    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
