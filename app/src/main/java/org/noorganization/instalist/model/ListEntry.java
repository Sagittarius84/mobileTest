package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class ListEntry extends SugarRecord<ListEntry> {

    ShoppingList mList;
    Product      mProduct;
    float        mAmount;
    boolean      mStriked;

    public ListEntry() {
        mList    = null;
        mProduct = null;
        mAmount  = 1.0f;
        mStriked = false;
    }


    public ListEntry(ShoppingList _list, Product _product, float _amount) {
        mList    = _list;
        mProduct = _product;
        mAmount  = _amount;
        mStriked = false;
    }

    public ListEntry(ShoppingList _list, Product _product, float _amount, boolean _striked) {
        mList    = _list;
        mProduct = _product;
        mAmount  = _amount;
        mStriked = _striked;
    }
}
