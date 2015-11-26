package org.noorganization.instalist.view.event;

import org.noorganization.instalist.model.Product;

import java.util.Map;

/**
 * Event for notifiying other fragments.
 *
 * Should be sent by {@link org.noorganization.instalist.view.fragment.ProductListDialogFragment}.
 */
public class ProductSelectMessage {
    public Map<Product, Float> mProducts;

    public ProductSelectMessage(Map<Product, Float> _products) {
        mProducts = _products;
    }
}
