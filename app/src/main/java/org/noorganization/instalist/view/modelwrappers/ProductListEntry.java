package org.noorganization.instalist.view.modelwrappers;

import org.noorganization.instalist.model.Product;

/**
 * Created by TS on 25.05.2015.
 */
public class ProductListEntry implements IBaseListEntry {
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

    @Override
    public eItemType getType() {
        return eItemType.PRODUCT_LIST_ENTRY;
    }
}
