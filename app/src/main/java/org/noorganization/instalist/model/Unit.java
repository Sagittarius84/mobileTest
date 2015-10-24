package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a unit of a product. For example: kilogram
 * Created by michi on 14.04.15.
 */
public class Unit extends SugarRecord<Unit> {

    public final static String TABLE_NAME = "unit";

    public static String COLUMN_ID = "_id";
    public static String COLUMN_NAME = "name";

    public static String ATTR_NAME = "m_name";

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NAME + " TEXT"
            + ");";

    public String mName;

    public Unit() {
        mName = "";
    }

    public Unit(String _name) {
        mName = _name;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Unit anotherUnit = (Unit) other;
        return mName.equals(anotherUnit.mName) && getId().compareTo(anotherUnit.getId()) == 0;
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
