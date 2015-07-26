package org.noorganization.instalist.view.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;

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

    private ProductListEntry(Parcel _In) {
        mProduct = Product.findById(Product.class, _In.readLong());
        mChecked = _In.readSparseBooleanArray().get(mProduct.getId().intValue());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel _Dest, int _Flags) {
        SparseBooleanArray booleanArray = new SparseBooleanArray(1);
        booleanArray.append(mProduct.getId().intValue(), mChecked);
        _Dest.writeSparseBooleanArray(booleanArray);
        _Dest.writeLong(mProduct.getId());
    }

    public static final Parcelable.Creator<ProductListEntry> CREATOR = new Parcelable.Creator<ProductListEntry>() {
        public ProductListEntry createFromParcel(Parcel _In) {
            return new ProductListEntry(_In);
        }

        public ProductListEntry[] newArray(int _Size) {
            return new ProductListEntry[_Size];
        }
    };
}
