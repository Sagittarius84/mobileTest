package org.noorganization.instalist.controller.event;

import org.noorganization.instalist.model.Product;

/**
 * Created by daMihe on 19.07.2015.
 */
public class ProductChangedMessage {
    public enum Change {
        CREATED,
        CHANGED,
        DELETED
    }

    public Change  mChange;
    public Product mProduct;

    public ProductChangedMessage(Change _change, Product _product) {
        mChange  = _change;
        mProduct = _product;
    }
}
