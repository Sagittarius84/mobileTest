package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a virtual category entry for a product. Since category usually make people think a
 * thing can only be in one category, the word "tag" is more fitting. For example: A tomato is
 * vegetarian. But it's also lactose-free (or it should be at least).
 * Created by michi on 14.04.15.
 */
public class TaggedProduct extends SugarRecord<TaggedProduct> {

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
}
