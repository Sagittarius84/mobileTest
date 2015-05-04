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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Product anotherProduct = (Product) o;

        if (Float.compare(anotherProduct.mDefaultAmount, mDefaultAmount) != 0) {
            return false;
        }
        if (Float.compare(anotherProduct.mStepAmount, mStepAmount) != 0) {
            return false;
        }
        if (!mName.equals(anotherProduct.mName)) {
            return false;
        }
        if ((mUnit == null && anotherProduct.mUnit != null) || (mUnit != null && !mUnit.equals(anotherProduct.mUnit))) {
            return false;
        }

        return getId().compareTo(anotherProduct.getId()) == 0;
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }


    @Override
    public String toString() {
        return "Product{" +
                "mName='" + mName + '\'' +
                ", mUnit=" + (mUnit == null ? "null" : "id:"+mUnit.getId()) +
                ", mDefaultAmount=" + mDefaultAmount +
                ", mStepAmount=" + mStepAmount +
                '}';
    }
}
