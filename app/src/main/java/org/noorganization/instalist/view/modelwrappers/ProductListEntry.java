/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;

/**
 * Wrapper for Products to strike/unstrike them.
 * Created by TS on 25.05.2015.
 */
public class ProductListEntry implements IBaseListEntry {

    public static final int CHECKED_PROPERTY = 0;
    private Product mProduct;
    private boolean mChecked;

    public ProductListEntry(Product _Product) {
        mProduct = _Product;
    }

    private ProductListEntry(Parcel _In) {
        mChecked = _In.readSparseBooleanArray().get(CHECKED_PROPERTY);
        mProduct = ControllerFactory.getProductController(GlobalApplication.getContext()).findById(_In.readString());
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
    public String getId() {
        return mProduct.mUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductListEntry)) return false;

        ProductListEntry that = (ProductListEntry) o;
        return mProduct.mUUID.equals(that.mProduct.mUUID) && eItemType.PRODUCT_LIST_ENTRY == ((IBaseListEntry)o).getType();
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
        booleanArray.append(CHECKED_PROPERTY, mChecked);
        _Dest.writeSparseBooleanArray(booleanArray);
        _Dest.writeString(mProduct.mUUID);
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
