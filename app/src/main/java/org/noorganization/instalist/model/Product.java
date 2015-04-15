package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Represents a product.
 * Created by michi on 14.04.15.
 */
public class Product extends SugarRecord<Product> {

    public String mName;
    /** The unit of the product. Can also be null if the products has no unit. */
    public Unit   mUnit;
    /** The default amount is usually 1.0f */
    public float  mDefaultAmount;
    /** The amount to increase or decrease over quick buttons. Usually 1.0f. */
    public float  mStepAmount;

    public Product() {
        mUnit          = null;
        mName          = "";
        mDefaultAmount = 1.0f;
        mStepAmount    = 1.0f;
    }


    public Product(String _name, Unit _unit, float _defaultAmount, float _stepAmount) {
        mUnit          = _unit;
        mName          = _name;
        mDefaultAmount = _defaultAmount;
        mStepAmount    = _stepAmount;
    }

    public Product(String _name, Unit _unit) {
        mUnit          = _unit;
        mName          = _name;
        mDefaultAmount = 1.0f;
        mStepAmount    = 1.0f;
    }
}
