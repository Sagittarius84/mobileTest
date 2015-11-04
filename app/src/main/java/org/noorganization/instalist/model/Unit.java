package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a unit of a product. For example: kilogram
 * Created by michi on 14.04.15.
 */
public class Unit extends SugarRecord<Unit> {

    public final static String TABLE_NAME = "unit";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN_NO_TABLE_PREFIXED {
        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NAME = "name";
        public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_NAME};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_TABLE_PREFIXED {
        public final static String COLUMN_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID);
        public final static String COLUMN_NAME = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME);
        public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_NAME};
    }


    /**
     * @deprecated  do not use anymore instead use {@link COLUMN_NO_TABLE_PREFIXED#COLUMN_NAME}
     */
    public static String ATTR_NAME = "m_name";

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME + " TEXT"
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
