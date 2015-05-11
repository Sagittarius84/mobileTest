package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a product written on a shoppinglist.
 * Created by michi on 14.04.15.
 */
public class ListEntry extends SugarRecord<ListEntry> {

    public ShoppingList mList;
    public Product      mProduct;
    /** The amount of product that's listed */
    public float        mAmount;
    /** Whether a product is struck through, i.e. because it's already bought. */
    public boolean      mStruck;

    public ListEntry() {
        mList    = null;
        mProduct = null;
        mAmount  = 1.0f;
        mStruck  = false;
    }


    public ListEntry(ShoppingList _list, Product _product, float _amount) {
        mList    = _list;
        mProduct = _product;
        mAmount  = _amount;
        mStruck  = false;
    }

    public ListEntry(ShoppingList _list, Product _product, float _amount, boolean _struck) {
        mList    = _list;
        mProduct = _product;
        mAmount  = _amount;
        mStruck  = _struck;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ListEntry that = (ListEntry) o;

        if (!getId().equals(that.getId()) ||
                Float.compare(that.mAmount, mAmount) != 0 ||
                mStruck != that.mStruck ||
                !mList.equals(that.mList) ||
                !mProduct.equals(that.mProduct)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }

    @Override
    public String toString() {
        return "ListEntry { id = " + getId() + ", mList.id = " + (mList == null ? "none" : mList.getId()) +
                ", mProduct.id = " + (mProduct == null ? "none" : mProduct.getId()) +
                ", mStruck = " + mStruck + ", mAmount" + mAmount + " }";
    }
}
