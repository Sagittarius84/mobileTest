package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * Created by michi on 14.04.15.
 */
public class Tag extends SugarRecord<Tag> {

    String mName;

    public Tag() {
        mName = "";
    }

    public Tag(String _name) {
        mName = _name;
    }
}
