package org.noorganization.instalist.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.provider.internal.IngredientProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.RecipeProvider;

public class IRecipeControllerTest extends AndroidTestCase {

    Recipe mCheeseCake;
    Recipe mPuffPastries;
    Product mFlour;
    Product mEgg;
    Product mCurd;
    Ingredient mIngredientFlourInCake;
    Ingredient mIngredientCurdInCake;

    IRecipeController mRecipeController;
    IProductController mProductController;

    ContentResolver mResolver;

    public void setUp() throws Exception {
        super.setUp();
        mRecipeController = ControllerFactory.getRecipeController(mContext);
        mProductController = ControllerFactory.getProductController(mContext);
        mResolver = mContext.getContentResolver();
        tearDown();

        mCheeseCake = mRecipeController.createRecipe("_TEST_cheesecake");
        assertNotNull(mCheeseCake);

        mPuffPastries = mRecipeController.createRecipe("_TEST_puffpastries");
        assertNotNull(mPuffPastries);

        mCurd = mProductController.createProduct("_TEST_curd", null, 1.0f, 1.0f);
        assertNotNull(mCurd);
        mFlour = mProductController.createProduct("_TEST_flour", null, 1.0f, 1.0f);
        assertNotNull(mFlour);
        mEgg = mProductController.createProduct("_TEST_egg", null, 1.0f, 1.0f);
        assertNotNull(mEgg);

        mIngredientFlourInCake = mRecipeController.addOrChangeIngredient(mCheeseCake, mFlour, 0.3f);
        assertNotNull(mIngredientFlourInCake);
        mIngredientCurdInCake = mRecipeController.addOrChangeIngredient(mCheeseCake, mCurd, 0.5f);
        assertNotNull(mIngredientCurdInCake);

    }

    public void tearDown() throws Exception {
        mResolver.delete(Uri.parse(IngredientProvider.MULTIPLE_INGREDIENT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(ProductProvider.MULTIPLE_PRODUCT_CONTENT_URI), null, null);
        mResolver.delete(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), null, null);
    }

    public void testCreateRecipe() throws Exception {
        assertNull(mRecipeController.createRecipe(null));
        assertNull(mRecipeController.createRecipe("_TEST_cheesecake"));

        Recipe returnedRecipe = mRecipeController.createRecipe("_TEST_pancakes");
        assertNotNull(returnedRecipe);
        assertEquals("_TEST_pancakes", returnedRecipe.mName);
        Recipe savedRecipe = mRecipeController.findById(returnedRecipe.mUUID);
        assertNotNull(savedRecipe);
        assertEquals(returnedRecipe, savedRecipe);
    }

    public void testRemoveRecipe() throws Exception {
        // nothing should happen.
        mRecipeController.removeRecipe(null);
        Recipe invalidRecipe = new Recipe("_TEST_pancakes");
        // also this should have no consequences.
        mRecipeController.removeRecipe(invalidRecipe);
        assertNotNull(mCheeseCake.mUUID);
        mRecipeController.removeRecipe(mCheeseCake);
        assertNull(mRecipeController.findById(mCheeseCake.mUUID));
        assertNotNull(mCheeseCake.mUUID);
        Cursor ingredientCursor = mResolver.query(Uri.parse(IngredientProvider.MULTIPLE_INGREDIENT_CONTENT_URI),
                Ingredient.COLUMN.ALL_COLUMNS,
                Ingredient.COLUMN.RECIPE_ID + "=?",
                new String[]{mCheeseCake.mUUID},
                null);
        assertNotNull(ingredientCursor);
        assertEquals(0, ingredientCursor.getCount());
        ingredientCursor.close();
    }

    public void testAddOrChangeIngredient() throws Exception {
        assertNull(mRecipeController.addOrChangeIngredient(mCheeseCake, null, 1.0f));
        assertNull(mRecipeController.addOrChangeIngredient(null, mEgg, 2.0f));
        assertNull(mRecipeController.addOrChangeIngredient(mCheeseCake, mEgg, -2.0f));

        Ingredient returnedCreatedIngredient = mRecipeController.addOrChangeIngredient(mCheeseCake,
                mEgg, 2.0f);
        assertNotNull(returnedCreatedIngredient);
        assertEquals(mCheeseCake, returnedCreatedIngredient.mRecipe);
        assertEquals(mEgg, returnedCreatedIngredient.mProduct);
        assertEquals(2.0f, returnedCreatedIngredient.mAmount, 0.001f);
        Ingredient savedCreatedIngredient = mRecipeController.findIngredientById(returnedCreatedIngredient.mUUID);
        assertNotNull(savedCreatedIngredient);
        assertEquals(returnedCreatedIngredient, savedCreatedIngredient);

        Ingredient returnedUnchangedIngredient = mRecipeController.addOrChangeIngredient(mCheeseCake,
                mFlour, -0.5f);
        assertNotNull(returnedUnchangedIngredient);
        assertEquals(mIngredientFlourInCake, returnedUnchangedIngredient);

        Ingredient returnedChangedIngredient = mRecipeController.addOrChangeIngredient(mCheeseCake,
                mFlour, 0.5f);
        assertNotNull(returnedChangedIngredient);
        assertEquals(0.5f, returnedChangedIngredient.mAmount);

    }

    public void testRemoveIngredient() throws Exception {
        // nothing should happen.
        mRecipeController.removeIngredient(null);
        assertNotNull(mRecipeController.findIngredientById(mIngredientFlourInCake.mUUID));

        mRecipeController.removeIngredient(mIngredientFlourInCake);
        // TODO why should that be notnull?
        //assertNotNull(mRecipeController.findIngredientById(mIngredientFlourInCake.mUUID));
        assertNull(mRecipeController.findIngredientById(mIngredientFlourInCake.mUUID));

        assertNotNull(mRecipeController.findIngredientById(mIngredientCurdInCake.mUUID));
        mRecipeController.removeIngredient(mCheeseCake, mCurd);
        // TODO why should that be notnull?
        //assertNotNull(mRecipeController.findIngredientById(mIngredientCurdInCake.mUUID));
        assertNull(mRecipeController.findIngredientById(mIngredientCurdInCake.mUUID));
    }

    public void testRenameRecipe() throws Exception {
        assertNull(mRecipeController.renameRecipe(null, "_TEST_pancakes"));

        Recipe returnedUnchangedRecipe = mRecipeController.renameRecipe(mCheeseCake, "_TEST_puffpastries");
        assertNotNull(returnedUnchangedRecipe);
        assertEquals(mCheeseCake, returnedUnchangedRecipe);

        Recipe returnedChangedRecipe = mRecipeController.renameRecipe(mCheeseCake, "_TEST_filledbread");
        assertNotNull(returnedChangedRecipe);
        assertEquals("_TEST_filledbread", returnedChangedRecipe.mName);

    }
}