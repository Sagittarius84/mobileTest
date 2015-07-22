package org.noorganization.instalist.view.modelwrappers;

import org.noorganization.instalist.model.Product;

/**
 * Wrapper for Products.
 * Created by TS on 25.05.2015.
 */
public class ProductListEntry implements IBaseListEntry {

    private Product mProduct;
    private boolean mChecked;

    public ProductListEntry(Product _Product) {
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
    public eItemType getType() {
        return eItemType.PRODUCT_LIST_ENTRY;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean _Checked) {
        mChecked = _Checked;
    }

    @Override
    public Object getItem() {
        return mProduct;
    }

    @Override
    public long getId() {
        return mProduct.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductListEntry)) return false;

        ProductListEntry that = (ProductListEntry) o;

        return mProduct.equals(that.mProduct);

    }

    @Override
    public int hashCode() {
        return mProduct.hashCode();
    }
}
