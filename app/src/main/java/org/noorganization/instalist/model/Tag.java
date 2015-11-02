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

    public final static String TABLE_NAME = "tag";

    public final static String LOCAL_COLUMN_ID = "_id";
    public final static String LOCAL_COLUMN_NAME = "name";

    public final static String COLUMN_ID = TABLE_NAME.concat("." + LOCAL_COLUMN_ID);
    public final static String COLUMN_NAME = TABLE_NAME.concat("." + LOCAL_COLUMN_NAME);

    public final static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_NAME};

    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + LOCAL_COLUMN_ID + " TEXT PRIMARY KEY,"
            + LOCAL_COLUMN_NAME + " TEXT"
            + ");";

    public String mName;

    // TODO: maybe a frequency parameter to track down usage?

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
