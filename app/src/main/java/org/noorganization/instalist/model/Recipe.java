package org.noorganization.instalist.model;

import com.orm.StringUtil;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Represents a logical recipe. Like ShoppingList, it does not contain a real java list.
 * Created by michi on 14.04.15.
 */
public class Recipe extends SugarRecord<Recipe> {

    /**
     * @deprecated use instead {@link COLUMN_PREFIXED#NAME}
     */
    public final static String ATTR_NAME = StringUtil.toSQLName("mName");

    public final static String TABLE_NAME = "recipe";

    /**
     * Column names that does not contain the table prefix.
     */
    public final static class COLUMN {
        public final static String ID = "_id";
        public final static String NAME = "name";
        public final static String[] ALL_COLUMNS = {ID, NAME};
    }

    /**
     * Column names that are prefixed with the table name. So like this TableName.ColumnName
     */
    public final static class COLUMN_PREFIXED {
        public final static String ID = TABLE_NAME.concat("." + COLUMN.ID);
        public final static String NAME = TABLE_NAME.concat("." + COLUMN.NAME);
        public final static String[] ALL_COLUMNS = {ID, NAME};
    }


    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME
            + "("
            + COLUMN.ID + " TEXT PRIMARY KEY,"
            + COLUMN.NAME + " TEXT"
            + ")";

    public String mName;

    public Recipe() {
        mName = "";
    }

    public Recipe(String _name) {
        mName = _name;
    }

    public List<Ingredient> getIngredients() {
        return Select.from(Ingredient.class).where(Condition.prop("m_recipe").eq(getId())).list();
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (this == anotherObject) {
            return true;
        }
        if (anotherObject == null || getClass() != anotherObject.getClass()) {
            return false;
        }

        Recipe anotherRecipe = (Recipe) anotherObject;

        return mName.equals(anotherRecipe.mName) && getId().compareTo(anotherRecipe.getId()) == 0;
    }

    @Override
    public int hashCode() {
        return getId().intValue();
    }
}
