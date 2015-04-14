package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class Unit extends SugarRecord<Unit> {

    String mName;

    public Unit() {
        mName = "";
    }

    public Unit(String _name) {
        mName = _name;
    }

}
