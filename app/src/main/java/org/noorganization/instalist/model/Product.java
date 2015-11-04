package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a product.
 * Created by michi on 14.04.15.
 */
public class Product extends SugarRecord<Product> {

    public final static String TABLE_NAME = "product";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN_NO_TABLE_PREFIXED {

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_UNIT_ID = "unit_id";
        public final static String COLUMN_DEFAULT_AMOUNT = "default_amount";
        public final static String COLUMN_STEP_AMOUNT = "step_amount";

        public final static String[] ALL_COLUMNS = new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_UNIT_ID,
                COLUMN_DEFAULT_AMOUNT,
                COLUMN_STEP_AMOUNT
        };
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_TABLE_PREFIXED {
        public final static String COLUMN_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID);
        public final static String COLUMN_NAME = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME);
        public final static String COLUMN_UNIT_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_UNIT_ID);
        public final static String COLUMN_DEFAULT_AMOUNT = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_DEFAULT_AMOUNT);
        public final static String COLUMN_STEP_AMOUNT = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_STEP_AMOUNT);

        public final static String[] ALL_COLUMNS = new String[]{
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_UNIT_ID,
                COLUMN_DEFAULT_AMOUNT,
                COLUMN_STEP_AMOUNT
        };
    }

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "(" + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + " TEXT PRIMARY KEY NOT NULL,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_UNIT_ID + " TEXT,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_DEFAULT_AMOUNT + " REAL,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_STEP_AMOUNT + " REAL,"
            + "FOREIGN KEY ( " + COLUMN_NO_TABLE_PREFIXED.COLUMN_UNIT_ID + ") REFERENCES " + Unit.TABLE_NAME + "(" + Unit.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + ")"
            + "ON DELETE SET NULL ON UPDATE NO ACTION"
            + ");";

    public String id;

    public String mName;
    /** The unit of the product. Can also be null if the products has no unit. */
    public Unit   mUnit;
    /** The default amount is usually 1.0f */
    public float  mDefaultAmount;
    /** The amount to increase or decrease over quick buttons. Usually 1.0f. */
    public float  mStepAmount;

    public Product() {
        mUnit          = null;
        mName          = "";
        mDefaultAmount = 1.0f;
        mStepAmount    = 1.0f;
    }


    public Product(String _name, Unit _unit, float _defaultAmount, float _stepAmount) {
        mUnit          = _unit;
        mName          = _name;
        mDefaultAmount = _defaultAmount;
        mStepAmount    = _stepAmount;
    }

    public Product(String _name, Unit _unit) {
        mUnit          = _unit;
        mName          = _name;
        mDefaultAmount = 1.0f;
        mStepAmount    = 1.0f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Product anotherProduct = (Product) o;

        if (Float.compare(anotherProduct.mDefaultAmount, mDefaultAmount) != 0) {
            return false;
        }
        if (Float.compare(anotherProduct.mStepAmount, mStepAmount) != 0) {
            return false;
        }
        if (!mName.equals(anotherProduct.mName)) {
            return false;
        }
        if ((mUnit == null && anotherProduct.mUnit != null) || (mUnit != null && !mUnit.equals(anotherProduct.mUnit))) {
            return false;
        }

        return getId().compareTo(anotherProduct.getId()) == 0;
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }


    @Override
    public String toString() {
        return "Product{" +
                "mName='" + mName + '\'' +
                ", mUnit=" + (mUnit == null ? "null" : "id:"+mUnit.getId()) +
                ", mDefaultAmount=" + mDefaultAmount +
                ", mStepAmount=" + mStepAmount +
                '}';
    }
}
