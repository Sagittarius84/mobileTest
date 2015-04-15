package org.noorganization.instalist.model;

import com.orm.SugarRecord;

/**
 * A pseudo-category for products. See {@link org.noorganization.instalist.model.TaggedProduct} for more details.
 * Created by michi on 14.04.15.
 */
public class Tag extends SugarRecord<Tag> {

    public String mName;

    public Tag() {
        mName = "";
    }

    public Tag(String _name) {
        mName = _name;
    }
}
