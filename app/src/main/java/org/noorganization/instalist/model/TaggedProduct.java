package org.noorganization.instalist.model;

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
     * @deprecated us instead {@link TaggedProduct#COLUMN_TAG_ID}
     */
    public final static String ATTR_TAG     = StringUtil.toSQLName("mTag");
    /**
     * @deprecated use instead {@link TaggedProduct#COLUMN_PRODUCT_ID}
     */
    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");

    public final static String TABLE_NAME = "taggedProduct";

    public final static String LOCAL_COLUMN_ID = "_id";
    public final static String LOCAL_COLUMN_TAG_ID = "tag_id";
    public final static String LOCAL_COLUMN_PRODUCT_ID = "product_id";

    public final static String COLUMN_ID = TABLE_NAME.concat("." + LOCAL_COLUMN_ID);
    public final static String COLUMN_TAG_ID = TABLE_NAME.concat("." + LOCAL_COLUMN_TAG_ID);
    public final static String COLUMN_PRODUCT_ID = TABLE_NAME.concat("." + LOCAL_COLUMN_PRODUCT_ID);

    /**
     * Will return all columns of this TaggedProduct Table.
     */
    public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TAG_ID, COLUMN_PRODUCT_ID};
    public final static String[] ALL_COLUMNS_JOINED = {Tag.COLUMN_ID, Tag.COLUMN_NAME,
            TaggedProduct.COLUMN_ID, TaggedProduct.COLUMN_TAG_ID, TaggedProduct.COLUMN_PRODUCT_ID,
            Product.COLUMN_ID, Product.COLUMN_NAME, Product.COLUMN_DEFAULT_AMOUNT,
            Product.COLUMN_STEP_AMOUNT, Product.COLUMN_UNIT_ID};

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + LOCAL_COLUMN_ID + " TEXT PRIMARY KEY,"
            + LOCAL_COLUMN_TAG_ID + " TEXT,"
            + LOCAL_COLUMN_PRODUCT_ID + " TEXT,"
            + "FOREIGN KEY (" + LOCAL_COLUMN_PRODUCT_ID + ") REFERENCES " + Product.TABLE_NAME + " (" + Product.LOCAL_COLUMN_ID + ") "
            + "ON UPDATE CASCADE ON DELETE CASCADE,"
            + "FOREIGN KEY (" + LOCAL_COLUMN_TAG_ID + ") REFERENCES " + Tag.TABLE_NAME + " (" + Tag.LOCAL_COLUMN_ID + ") "
            + "ON UPDATE CASCADE ON DELETE CASCADE"
            + ")";

    public Tag     mTag;
    public Product mProduct;

    /**
     * Constructor for initialized to null values.
     */
    public TaggedProduct() {
        mTag     = null;
        mProduct = null;
    }

    /**
     * Constructor of TaggedProduct to combine product with a tag.
     * @param _tag the tag to be assigned.
     * @param _product the product to be connected with the tag.
     */
    public TaggedProduct(Tag _tag, Product _product) {
        mTag     = _tag;
        mProduct = _product;
    }

    /**
     * Retrieves all tagged products for the given _product.
     * @deprecated it seems to be a weird method
     * @param _product the product where the associating TaggedProduct should be found.
     * @return the TaggedProduct when found, else empty TaggedProductList.
     */
    public static List<TaggedProduct> findTaggedProductsByProduct(Product _product){
        return TaggedProduct.find(TaggedProduct.class,"m_product = ?", _product.getId().toString());
    }
}
