package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class Product extends SugarRecord<Product> {

    String mName;
    Unit   mUnit;
    float  mDefaultAmount;
    float  mStepAmount;

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
