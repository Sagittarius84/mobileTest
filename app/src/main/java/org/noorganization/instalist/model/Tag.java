package org.noorganization.instalist.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.LinkedList;
import java.util.List;

/**
 * A pseudo-category for products. See {@link org.noorganization.instalist.model.TaggedProduct} for
 * more details.
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

    public List<Product> findProducts() {
        List<TaggedProduct> taggedProductList = Select.from(TaggedProduct.class).
                where(Condition.prop("m_tag").eq(getId())).list();
        List<Product> rtn = new LinkedList<>();

        for (TaggedProduct currentTaggedProduct : taggedProductList) {
            rtn.add(currentTaggedProduct.mProduct);
        }

        return rtn;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }

        Tag otherTag = (Tag) otherObject;

        return mName.equals(otherTag.mName) && getId().compareTo(otherTag.getId()) == 0;

    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
