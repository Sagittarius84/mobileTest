package org.noorganization.instalist.controller.event;

import org.noorganization.instalist.model.Recipe;

/**
 * Event class for notifying about changes in recipes.
 * Created by daMihe on 05.07.2015.
 */
public class RecipeChangedMessage {
    public enum Change {
        CREATED,
        CHANGED,
        DELETED
    }

    public Recipe mRecipe;
    public Change mChange;

    public RecipeChangedMessage(Recipe _recipe, Change _change) {
        mRecipe = _recipe;
        mChange = _change;
    }
}
