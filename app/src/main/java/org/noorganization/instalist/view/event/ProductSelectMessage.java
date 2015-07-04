package org.noorganization.instalist.view.event;

import org.noorganization.instalist.model.Product;

import java.util.Map;

public class ProductSelectMessage {
    public Map<Product, Float> mProducts;

    public ProductSelectMessage(Map<Product, Float> _products) {
        mProducts = _products;
    }
}
