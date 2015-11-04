package org.noorganization.instalist.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.provider.internal.IInternalProvider;
import org.noorganization.instalist.provider.internal.ProductProvider;
import org.noorganization.instalist.provider.internal.RecipeProvider;

import java.util.UUID;

/**
 * Created by Tino on 28.10.2015.
 */
public class RecipeProviderTest extends AndroidTestCase {

    IInternalProvider mRecipeProvider;
    IInternalProvider mProductProvider;

    SQLiteDatabase mDatabase;

    @Override
    public void setUp() {
        mDatabase = new DBOpenHelper(getContext(), null).getWritableDatabase();
        mRecipeProvider = new RecipeProvider(getContext());
        mProductProvider = new ProductProvider(getContext());
        mRecipeProvider.onCreate(mDatabase);
        mProductProvider.onCreate(mDatabase);
    }

    @Override
    public void tearDown() {
        mDatabase.close();
    }

    private void resetDb() {
        mDatabase.delete(Recipe.TABLE_NAME, null, null);
        mDatabase.delete(Product.TABLE_NAME, null, null);
    }

    public void testQueryMultipleRecipes() {
        Uri multipleRecipesUri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI);
        Cursor noProducts = mRecipeProvider.query(multipleRecipesUri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(noProducts);
        assertEquals(0, noProducts.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid,
                "TestRecipe1"
        });

        Cursor productCursor = mRecipeProvider.query(multipleRecipesUri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid2,
                "TestRecipe2"
        });

        productCursor = mRecipeProvider.query(multipleRecipesUri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME + " ASC");
        assertNotNull(productCursor);
        assertEquals(2, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));
        productCursor.moveToNext();
        assertEquals("TestRecipe2", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));
        resetDb();
    }

    public void testQuerySingleRecipe() {
        Cursor noCategory = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*",
                UUID.randomUUID().toString())), Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);

        assertNotNull(noCategory);
        assertEquals(0, noCategory.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid,
                "TestRecipe1"
        });

        Cursor productCursor = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*",
                uuid)), Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid2,
                "TestRecipe2"
        });

        productCursor = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid2)),
                Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe2", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));
        resetDb();
    }

    public void testGetTypeSingleRecipe() {
        String type = mRecipeProvider.getType(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", "0")));
        assertEquals(ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + RecipeProvider.RECIPE_BASE_TYPE, type);

        type = mRecipeProvider.getType(Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceAll("\\*", "0")));
        assertEquals(ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + RecipeProvider.INGREDIENT_BASE_TYPE, type);
    }

    public void testGetTypeMultipleRecipes() {
        String type = mRecipeProvider.getType(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI));
        assertEquals(type, ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + RecipeProvider.RECIPE_BASE_TYPE);
        type = mRecipeProvider.getType(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceAll("\\*", "0")));
        assertEquals(ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + RecipeProvider.INGREDIENT_BASE_TYPE, type);
    }

    public void testInsertSingleRecipe() {
        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();
        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid, "TestRecipe");
        assertNotNull(uri);

        String pseudoUri = RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid);
        assertNotNull(uri);
        assertEquals(pseudoUri, uri.toString());

        Cursor cursor = mRecipeProvider.query(uri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_ID)));
        assertEquals("TestRecipe", cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        resetDb();
    }


    public void testDeleteSingleRecipe() {

        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();
        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid, "TestRecipe");
        assertNotNull(uri);

        int affectedRows = mRecipeProvider.delete(uri, null, null);
        assertEquals(1, affectedRows);
        Cursor cursor = mRecipeProvider.query(uri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
    }

    public void testDeleteMultipleRecipes() {
        ContentValues contentValues = new ContentValues();

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid, "TestRecipe1");
        assertNotNull(uri);
        Uri uri2 = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid2, "TestRecipe2");
        assertNotNull(uri2);

        int affectedRows = mRecipeProvider.delete(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME + " LIKE ?", new String[]{"%TestRecipe%"});

        assertEquals(2, affectedRows);

        uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid, "TestRecipe1");
        assertNotNull(uri);
        uri2 = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid2, "TestRecipe2");
        assertNotNull(uri2);

        affectedRows = mRecipeProvider.delete(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME + " LIKE ?", new String[]{"%TestRecipe1%"});

        assertEquals(1, affectedRows);
    }

    public void testUpdateSingleRecipe() {
        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuid, "TestRecipe1");
        assertNotNull(uri);

        contentValues.put(Recipe.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME, "TestRecipe2");
        int affectedRows = mRecipeProvider.update(uri, contentValues, null, null);

        assertEquals(1, affectedRows);

        Cursor cursor = mRecipeProvider.query(uri, Recipe.COLUMN_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();

        assertEquals("TestRecipe2", cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_TABLE_PREFIXED.COLUMN_NAME)));
        cursor.close();
    }


    public void testInsertSingleIngredient() {

        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuidRecipe, "TestRecipe1");
        assertNotNull(uri);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_NO_TABLE_PREFIXED.COLUMN_UNIT_ID, (String) null);

        Uri productUri = mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);
        assertNotNull(productUri);

        String uuid = UUID.randomUUID().toString();
        uri = uri.buildUpon().appendPath("ingredient").appendPath(uuid).build();

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT, 1.0f);

        Uri ingredientUri = mRecipeProvider.insert(uri, ingredientValues);
        uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        assertNotNull(ingredientUri);
        assertEquals(true, uri.compareTo(ingredientUri) == 0);

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID)));

    }


    public void testQuerySingleIngredient() {
        String uuidRecipe = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{uuidRecipe, "TestRecipe1"});
        String uuidProduct = UUID.randomUUID().toString();

        Uri productUri = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct, "TestProduct", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri);

        String uuid = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid, uuidProduct, uuidRecipe, String.valueOf(1.0f)});

        // Uri ingredientUri = mTaggedProductProvider.insert(uri,ingredientValues);
        Uri uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        assertNotNull(uri);

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID)));
        cursor.close();
    }

    public void testQueryMultipleIngredients() {
        String uuidRecipe = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{uuidRecipe, "TestRecipe1"});

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct1 = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        Uri productUri1 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct1, "TestProduct1", 0.5f, 0.5f, (String) null);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct2, "TestProduct2", 0.5f, 0.5f, (String) null);

        assertNotNull(productUri1);
        assertNotNull(productUri2);

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid, uuidProduct1, uuidRecipe, String.valueOf(1.0f)});
        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid2, uuidProduct2, uuidRecipe, String.valueOf(2.0f)});

        Uri uri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe));

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT + " ASC");

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(2, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID)));
        assertEquals(uuidProduct1, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID)));
        assertTrue(cursor.moveToNext());
        assertEquals(uuid2, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID)));
        assertEquals(uuidProduct2, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID)));
        cursor.close();
    }

    public void testDeleteSingleIngredient() {

        String uuidRecipe = UUID.randomUUID().toString();

        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuidRecipe, "TestRecipe1");
        assertNotNull(uri);

        String uuidProduct = UUID.randomUUID().toString();
        Uri productUri1 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct, "TestProduct", 0.5f, 0.5f, (String) null);
        assertNotNull(productUri1);

        String uuid = UUID.randomUUID().toString();
        uri = uri.buildUpon().appendPath("ingredient").appendPath(uuid).build();

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT, 1.0f);

        Uri ingredientUri = mRecipeProvider.insert(uri, ingredientValues);
        uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        assertNotNull(ingredientUri);

        int affectedRows = mRecipeProvider.delete(ingredientUri, null, null);

        assertEquals(1, affectedRows);
        uri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe));

        // check if the entries are really removed.
        Cursor cursor = mRecipeProvider.query(uri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT + " ASC");
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testDeleteMultipleIngredient() {

        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuidRecipe, "TestRecipe1");
        assertNotNull(uri);

        String uuidProduct = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        Uri productUri1 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct, "TestProduct", 0.5f, 0.5f, (String) null);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct2, "TestProduct2", 0.5f, 0.5f, (String) null);

        assertNotNull(productUri1);
        assertNotNull(productUri2);

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Uri uri1 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        Uri uri2 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid2));

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT, 1.0f);

        mRecipeProvider.insert(uri1, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid2);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct2);
        mRecipeProvider.insert(uri2, ingredientValues);

        Uri recipeUri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe));

        int affectedRows = mRecipeProvider.delete(recipeUri, null, null);

        assertEquals(2, affectedRows);
        // check if the entries are really removed.
        Cursor cursor = mRecipeProvider.query(recipeUri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT + " ASC");
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testUpdateSingleIngredient() {
        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();
        Uri uri = ProviderTestUtils.insertRecipe(mRecipeProvider, uuidRecipe, "TestRecipe1");
        assertNotNull(uri);

        String uuidProduct = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        Uri productUri1 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct, "TestProduct", 0.5f, 0.5f, (String) null);
        Uri productUri2 = ProviderTestUtils.insertProduct(mProductProvider, uuidProduct2, "TestProduct2", 0.5f, 0.5f, (String) null);

        assertNotNull(productUri1);
        assertNotNull(productUri2);

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Uri uri1 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        Uri uri2 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid2));

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT, 1.0f);

        mRecipeProvider.insert(uri1, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid2);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_PRODUCT_ID, uuidProduct2);
        mRecipeProvider.insert(uri2, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT, 5.0f);

        Uri ingredientUri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        int affectedRows = mRecipeProvider.update(ingredientUri,
                ingredientValues, null, null
        );

        assertEquals(1, affectedRows);

        Cursor cursor = mRecipeProvider.query(ingredientUri, Ingredient.COLUMN_NO_TABLE_PREFIXED.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_ID)));
        assertEquals(5.0f, cursor.getFloat(cursor.getColumnIndex(Ingredient.COLUMN_NO_TABLE_PREFIXED.COLUMN_AMOUNT)));
        cursor.close();

    }
}
