package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

/**
 * Represents a virtual category entry for a product. Since category usually make people think a
 * thing can only be in one category, the word "tag" is more fitting. For example: A tomato is
 * vegetarian. But it's also lactose-free (or it should be at least).
 * Created by michi on 14.04.15.
 */
public class TaggedProduct extends SugarRecord<TaggedProduct> {
    public final static String ATTR_TAG     = StringUtil.toSQLName("mTag");
    public final static String ATTR_PRODUCT = StringUtil.toSQLName("mProduct");

    public Tag     mTag;
    public Product mProduct;

    public TaggedProduct() {
        mTag     = null;
        mProduct = null;
    }

    public TaggedProduct(Tag _tag, Product _product) {
        mTag     = _tag;
        mProduct = _product;
    }

    /**
     * Retrieves all tagged products for the given _product.
     * @param _product the product where the associating TaggedProduct should be found.
     * @return the TaggedProduct when found, else empty TaggedProductList.
     */
    public static List<TaggedProduct> findTaggedProductsByProduct(Product _product){
        return TaggedProduct.find(TaggedProduct.class,"m_product = ?", _product.getId().toString());
    }
}
