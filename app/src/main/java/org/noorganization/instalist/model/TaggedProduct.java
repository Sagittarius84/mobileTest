package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class TaggedProduct extends SugarRecord<TaggedProduct> {

    Tag     mTag;
    Product mProduct;

    public TaggedProduct() {
        mTag     = null;
        mProduct = null;
    }

    public TaggedProduct(Tag _tag, Product _product) {
        mTag     = _tag;
        mProduct = _product;
    }
}
