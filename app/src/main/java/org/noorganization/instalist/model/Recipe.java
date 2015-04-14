package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class Recipe extends SugarRecord<Recipe> {

    String mName;

    public Recipe() {
        mName = "";
    }

    public Recipe(String _name) {
        mName = _name;
    }
}
