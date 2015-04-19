package org.noorganization.instalist.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Represents a logical recipe. Like ShoppingList, it does not contain a real java list.
 * Created by michi on 14.04.15.
 */
public class Recipe extends SugarRecord<Recipe> {

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
}
