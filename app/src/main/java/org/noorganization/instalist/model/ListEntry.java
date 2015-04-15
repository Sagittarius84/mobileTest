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
}
