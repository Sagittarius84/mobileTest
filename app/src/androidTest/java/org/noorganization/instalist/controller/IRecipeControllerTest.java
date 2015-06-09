package org.noorganization.instalist.controller;

import android.test.AndroidTestCase;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;

public class IRecipeControllerTest extends AndroidTestCase {

    Recipe mCheeseCake;
    Recipe mPuffPastries;
    Product mFlour;
    Product mEgg;
    Product mCurd;
    Ingredient mFlourInCake;
    Ingredient mCurdInCake;

    IRecipeController mRecipeController;

    public void setUp() throws Exception {
        super.setUp();

        mCheeseCake = new Recipe("_TEST_cheesecake");
        mCheeseCake.save();
        mPuffPastries = new Recipe("_TEST_puffpastries");
        mPuffPastries.save();

        mCurd = new Product("_TEST_curd", null);
        mCurd.save();
        mFlour = new Product("_TEST_flour", null);
        mFlour.save();
        mEgg = new Product("_TEST_egg", null);
        mEgg.save();

        mFlourInCake = new Ingredient(mFlour, mCheeseCake, 0.3f);
        mFlourInCake.save();
        mCurdInCake = new Ingredient(mCurd, mCheeseCake, 0.5f);
        mCurdInCake.save();

        mRecipeController = ControllerFactory.getRecipeController();
    }

    public void tearDown() throws Exception {
        SugarRecord.deleteAll(Ingredient.class, "m_product = ? or m_product = ? or m_product = ? " +
                        "or m_recipe = ? or m_recipe = ?",
                mFlour.getId()+"", mEgg.getId()+"", mCurd.getId()+"", mCheeseCake.getId()+"",
                mPuffPastries.getId()+"");

        SugarRecord.deleteAll(Recipe.class, "m_name LIKE '_TEST_%'");
        SugarRecord.deleteAll(Product.class, "m_name LIKE '_TEST_%'");
    }

    public void testCreateRecipe() throws Exception {
        assertNull(mRecipeController.createRecipe(null));
        assertNull(mRecipeController.createRecipe("_TEST_cheesecake"));

        Recipe returnedRecipe = mRecipeController.createRecipe("_TEST_pancakes");
        assertNotNull(returnedRecipe);
        assertEquals("_TEST_pancakes", returnedRecipe.mName);
        Recipe savedRecipe = SugarRecord.findById(Recipe.class, returnedRecipe.getId());
        assertNotNull(savedRecipe);
        assertEquals(returnedRecipe, savedRecipe);
    }

    public void testRemoveRecipe() throws Exception {
        // nothing should happen.
        mRecipeController.removeRecipe(null);
        Recipe invalidRecipe = new Recipe("_TEST_pancakes");
        // also this should have no consequences.
        mRecipeController.removeRecipe(invalidRecipe);

        mRecipeController.removeRecipe(mCheeseCake);
        assertNull(SugarRecord.findById(Recipe.class, mCheeseCake.getId()));
        assertEquals(0, Select.from(Ingredient.class).
                where(Condition.prop("m_recipe").eq(mCheeseCake.getId())).count());
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
        Ingredient savedCreatedIngredient = SugarRecord.findById(Ingredient.class,
                returnedCreatedIngredient.getId());
        assertNotNull(savedCreatedIngredient);
        assertEquals(returnedCreatedIngredient, savedCreatedIngredient);

        Ingredient returnedUnchangedIngredient = mRecipeController.addOrChangeIngredient(mCheeseCake,
                mFlour, -0.5f);
        assertNotNull(returnedUnchangedIngredient);
        assertEquals(mFlourInCake, returnedUnchangedIngredient);

        Ingredient returnedChangedIngredient = mRecipeController.addOrChangeIngredient(mCheeseCake,
                mFlour, 0.5f);
        assertNotNull(returnedChangedIngredient);
        assertEquals(0.5f, returnedChangedIngredient.mAmount);

    }

    public void testRemoveIngredient() throws Exception {
        // nothing should happen.
        mRecipeController.removeIngredient(null);
        assertNotNull(SugarRecord.findById(Ingredient.class, mFlourInCake.getId()));

        mRecipeController.removeIngredient(mFlourInCake);
        assertNull(SugarRecord.findById(Ingredient.class, mFlourInCake.getId()));

        mRecipeController.removeIngredient(mCheeseCake, mCurd);
        assertNull(SugarRecord.findById(Ingredient.class, mCurdInCake.getId()));
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