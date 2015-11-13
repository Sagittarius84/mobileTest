package org.noorganization.instalist.model;

import android.content.ContentValues;

import com.orm.StringUtil;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Represents a virtual category entry for a product. Since category usually make people think a
 * thing can only be in one category, the word "tag" is more fitting. For example: A tomato is
 * vegetarian. But it's also lactose-free (or it should be at least).
 * Created by michi on 14.04.15.
 */
public class TaggedProduct extends SugarRecord<TaggedProduct> {
    /**
     * @deprecated us instead {@link COLUMN_TABLE_PREFIXED#COLUMN_TAG_ID}
     */
    public final static String ATTR_TAG = StringUtil.toSQLName("mTag");
    /**
     * @deprecated use instead {@link COLUMN_TABLE_PREFIXED#COLUMN_PRODUCT_ID}
     */
    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");

    public final static String TABLE_NAME = "taggedProduct";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN_NO_TABLE_PREFIXED {

        public final static String COLUMN_ID = "_id";
        public final static String COLUMN_TAG_ID = "tag_id";
        public final static String COLUMN_PRODUCT_ID = "product_id";
        /**
         * Will return all columns of this TaggedProduct Table.
         */
        public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TAG_ID, COLUMN_PRODUCT_ID};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_TABLE_PREFIXED {

        public final static String COLUMN_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID);
        public final static String COLUMN_TAG_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_TAG_ID);
        public final static String COLUMN_PRODUCT_ID = TABLE_NAME.concat("." + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID);

        public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TAG_ID, COLUMN_PRODUCT_ID};
    }

    /**
     * Returns the column names with qualified table names.
     */
    public final static String[] ALL_COLUMNS_JOINED = {Tag.COLUMN_PREFIXED.ID, Tag.COLUMN_PREFIXED.NAME,
            COLUMN_TABLE_PREFIXED.COLUMN_ID, COLUMN_TABLE_PREFIXED.COLUMN_TAG_ID, COLUMN_TABLE_PREFIXED.COLUMN_PRODUCT_ID,
            Product.PREFIXED_COLUMN.ID, Product.PREFIXED_COLUMN.NAME, Product.PREFIXED_COLUMN.DEFAULT_AMOUNT,
            Product.PREFIXED_COLUMN.STEP_AMOUNT, Product.PREFIXED_COLUMN.UNIT};

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_ID + " TEXT PRIMARY KEY,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_TAG_ID + " TEXT,"
            + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID + " TEXT,"
            + "FOREIGN KEY (" + COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID + ") REFERENCES " + Product.TABLE_NAME + " (" + Product.COLUMN.ID + ") "
            + "ON UPDATE CASCADE ON DELETE CASCADE,"
            + "FOREIGN KEY (" + COLUMN_NO_TABLE_PREFIXED.COLUMN_TAG_ID + ") REFERENCES " + Tag.TABLE_NAME + " (" + Tag.COLUMN.ID + ") "
            + "ON UPDATE CASCADE ON DELETE CASCADE"
            + ")";


    public String mUUID;
    public Tag mTag;
    public Product mProduct;

    /**
     * Constructor for initialized to null values.
     */
    public TaggedProduct() {
        mTag = null;
        mProduct = null;
    }

    /**
     * Constructor of TaggedProduct to combine product with a tag.
     *
     * @param _tag     the tag to be assigned.
     * @param _product the product to be connected with the tag.
     */
    public TaggedProduct(Tag _tag, Product _product) {
        mTag = _tag;
        mProduct = _product;
    }

    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, this.mUUID);
        cv.put(COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, this.mProduct != null ? this.mProduct.mUUID : null);
        cv.put(COLUMN_NO_TABLE_PREFIXED.COLUMN_TAG_ID, this.mTag != null ? this.mTag.mUUID : null);
        return cv;
    }

}
