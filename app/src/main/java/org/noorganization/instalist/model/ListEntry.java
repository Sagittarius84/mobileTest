package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;

/**
 * Represents a product written on a shoppinglist.
 * Created by michi on 14.04.15.
 */
public class ListEntry extends SugarRecord<ListEntry> {

    public static final String TABLE_NAME = "listentry";


    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN_NO_TABLE_PREFIXED {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_LIST = "list";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_PRODUCT = "product";
        public static final String COLUMN_STRUCK = "struck";

        public static final String ALL_COLUMNS[] = {COLUMN_ID, COLUMN_AMOUNT, COLUMN_LIST, COLUMN_PRIORITY, COLUMN_PRODUCT, COLUMN_STRUCK};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_TABLE_PREFIXED {
        public static final String COLUMN_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID);
        public static final String COLUMN_AMOUNT = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT);
        public static final String COLUMN_LIST = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_LIST);
        public static final String COLUMN_PRIORITY = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRIORITY);
        public static final String COLUMN_PRODUCT = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT);
        public static final String COLUMN_STRUCK = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_STRUCK);

        public static final String ALL_COLUMNS[] = {COLUMN_ID, COLUMN_AMOUNT, COLUMN_LIST, COLUMN_PRIORITY, COLUMN_PRODUCT, COLUMN_STRUCK};
    }



    public static final String DB_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, " +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT + " REAL NOT NULL DEFAULT 1, " +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT + " TEXT NOT NULL, " +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_LIST + " TEXT NOT NULL, " +
            COLUMN_NO_TABLE_PREFIXED.COLUMN_STRUCK + " INTEGER NOT NULL DEFAULT 0, " +
            "FOREIGN KEY (" + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT + ") REFERENCES " + Product.TABLE_NAME + " (" +
                Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
            "FOREIGN KEY (list) REFERENCES list (_id) ON UPDATE CASCADE ON DELETE CASCADE)";

    public final static String ATTR_LIST     = StringUtil.toSQLName("mList");
    public final static String ATTR_PRODUCT  = StringUtil.toSQLName("mProduct");
    public final static String ATTR_AMOUNT   = StringUtil.toSQLName("mAmount");
    public final static String ATTR_STRUCK   = StringUtil.toSQLName("mStruck");
    public final static String ATTR_PRIORITY = StringUtil.toSQLName("mPriority");

    public ShoppingList mList;
    public Product      mProduct;
    /** The amount of product that's listed */
    public float        mAmount;
    /** Whether a product is struck through, i.e. because it's already bought. */
    public boolean      mStruck;
    public int          mPriority;

    public ListEntry() {
        mList     = null;
        mProduct  = null;
        mAmount   = 1.0f;
        mStruck   = false;
        mPriority = 0;
    }


    public ListEntry(ShoppingList _list, Product _product, float _amount) {
        mList     = _list;
        mProduct  = _product;
        mAmount   = _amount;
        mStruck   = false;
        mPriority = 0;
    }

    public ListEntry(ShoppingList _list, Product _product, float _amount, boolean _struck) {
        mList     = _list;
        mProduct  = _product;
        mAmount   = _amount;
        mStruck   = _struck;
        mPriority = 0;
    }

    public ListEntry(ShoppingList _list, Product _product, float _amount, boolean _struck, int _prio) {
        mList     = _list;
        mProduct  = _product;
        mAmount   = _amount;
        mStruck   = _struck;
        mPriority = _prio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ListEntry that = (ListEntry) o;

        if (!getId().equals(that.getId()) ||
                Float.compare(that.mAmount, mAmount) != 0 ||
                mStruck != that.mStruck ||
                (mList == null && that.mList != null) || !mList.equals(that.mList) ||
                !mProduct.equals(that.mProduct) ||
                mPriority != that.mPriority) {
            return false;
        }

        return true;
    }

    public static ListEntry getListEntryById(long _Id){
        return ListEntry.findById(ListEntry.class, _Id);
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }

    @Override
    public String toString() {
        return "ListEntry { id = " + getId() + ", mList.id = " + (mList == null ? "none" : mList.getId()) +
                ", mProduct.id = " + (mProduct == null ? "none" : mProduct.getId()) +
                ", mStruck = " + mStruck + ", mAmount" + mAmount + " }";
    }
}
