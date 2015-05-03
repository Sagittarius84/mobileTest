package org.noorganization.instalist.model;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Represents a unit of a product. For example: kilogram
 * Created by michi on 14.04.15.
 */
public class Unit extends SugarRecord<Unit> {

    public String mName;

    public Unit() {
        mName = "";
    }

    public Unit(String _name) {
        mName = _name;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Unit anotherUnit = (Unit) other;

        return mName.equals(anotherUnit.mName);
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
