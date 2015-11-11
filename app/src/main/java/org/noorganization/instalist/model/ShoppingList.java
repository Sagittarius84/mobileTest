package org.noorganization.instalist.model;

import android.net.Uri;

import com.orm.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a shoppinglist itself as a logical object. This object does not contain a java list.
 * Created by michi on 14.04.15.
 */
public class ShoppingList {

    public static final String TABLE_NAME = "list";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN {
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";

        public static final String ALL_COLUMNS[] = {ID, NAME, CATEGORY};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class PREFIXED_COLUMN {
        public static final String ID = TABLE_NAME.concat("." + COLUMN.ID);
        public static final String NAME = TABLE_NAME.concat("." + COLUMN.NAME);
        public static final String CATEGORY = TABLE_NAME.concat("." + COLUMN.CATEGORY);
        public static final String ALL_COLUMNS[] = {ID, NAME, CATEGORY};
    }


    public static final String DB_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN.ID + " TEXT PRIMARY KEY NOT NULL, " +
            COLUMN.NAME + " TEXT NOT NULL, " +
            COLUMN.CATEGORY + " TEXT, " +
            "FOREIGN KEY (" + COLUMN.CATEGORY + ") REFERENCES " + Category.TABLE_NAME+
            " (" + Category.COLUMN.ID + ") ON UPDATE CASCADE ON DELETE CASCADE)";

    public final static String ATTR_NAME = StringUtil.toSQLName("mName");
    public final static String ATTR_CATEGORY = StringUtil.toSQLName("mCategory");

    public String   mUUID;
    public String   mName;
    public Category mCategory;

    public ShoppingList() {
        mUUID     = null;
        mName     = "";
        mCategory = null;
    }

    public ShoppingList(String _uuid, String _name) {
        mUUID     = _uuid;
        mName     = _name;
        mCategory = null;
    }

    public ShoppingList(String _uuid, String _name, Category _category) {
        mUUID = _uuid;
        mName = _name;
        mCategory = _category;
    }

    public List<ListEntry> getEntries() {
        // TODO move to controller.
        return new ArrayList<>(0);
        //return Select.from(ListEntry.class).where(Condition.prop("m_list").eq(getId())).list();
    }

    /**
     * Searches a ShoppingList by name. The name has to match exactly, or nothing will be found.
     * @param _name The name of the list. Any String but not null.
     * @return Either the found list or null if no list is matching.
     */
    public static ShoppingList findByName(String _name) {
        // TODO move to controller.

        if (_name == null) {
            return null;
        }

        //return Select.from(ShoppingList.class).where(Condition.prop("m_name").eq(_name)).first();
        return null;
    }

    /**
     * Adds all listnames to a list.
     * @return a list with the current shoppingListNames.
     */
    public static List<String> getShoppingListNames(){
        // TODO move to controller.
        return new ArrayList<>(0);
        /*
        List<ShoppingList> shoppingLists = Select.from(ShoppingList.class).list();
        List<String> shoppingListNames = new ArrayList<>();

        for (ShoppingList shoppingList : shoppingLists) {
            shoppingListNames.add(shoppingList.mName);
        }

        return shoppingListNames;*/
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

        if ((mCategory == null && that.mCategory != null) ||
                (mCategory != null && !mCategory.equals(that.mCategory))) {
            return false;
        }

        return (mUUID.equals(that.mUUID) && mName.equals(that.mName));

    }

    @Override
    public int hashCode() {
        return (int) mUUID.getLeastSignificantBits();
    }

    public Uri toUri(Uri _baseUri) {
        if (mUUID == null) {
            return null;
        }

        return Uri.withAppendedPath(_baseUri, "category/" +
                (mCategory == null ? "-" : mCategory.mUUID.toString()) + "/list/" +
                mUUID.toString());
    }
}
