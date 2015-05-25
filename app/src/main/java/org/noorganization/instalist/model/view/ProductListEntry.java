package org.noorganization.instalist.model.view;

import org.noorganization.instalist.model.Product;

/**
 * Created by TS on 25.05.2015.
 */
public class ProductListEntry extends BaseItemListEntry {
    private Product mProduct;

    public ProductListEntry(Product _Product){
        mProduct = _Product;
    }

    @Override
    public String getName() {
        return mProduct.mName;
    }

    @Override
    public void setName(String _Name) {
        mProduct.mName = _Name;
    }

    @Override
    public BaseItemReturnType getEntry() {
        return new BaseItemReturnType(mProduct);
    }
}
