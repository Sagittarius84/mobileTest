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
        Cursor noProducts = mRecipeProvider.query(multipleRecipesUri, Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(noProducts);
        assertEquals(0, noProducts.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid,
                "TestRecipe1"
        });

        Cursor productCursor = mRecipeProvider.query(multipleRecipesUri, Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid2,
                "TestRecipe2"
        });

        productCursor = mRecipeProvider.query(multipleRecipesUri, Recipe.ALL_COLUMNS, null, null, Recipe.COLUMN_NAME + " ASC");
        assertNotNull(productCursor);
        assertEquals(2, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_ID)));
        productCursor.moveToNext();
        assertEquals("TestRecipe2", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_ID)));
        resetDb();
    }

    public void testQuerySingleRecipe() {
        Cursor noCategory = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*",
                UUID.randomUUID().toString())), Recipe.ALL_COLUMNS, null, null, null);

        assertNotNull(noCategory);
        assertEquals(0, noCategory.getCount());

        String uuid = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid,
                "TestRecipe1"
        });

        Cursor productCursor = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*",
                uuid)), Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe1", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_NAME)));
        assertEquals(uuid, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_ID)));

        String uuid2 = UUID.randomUUID().toString();
        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{
                uuid2,
                "TestRecipe2"
        });

        productCursor = mRecipeProvider.query(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid2)),
                Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(productCursor);
        assertEquals(1, productCursor.getCount());
        productCursor.moveToFirst();
        assertEquals("TestRecipe2", productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_NAME)));
        assertEquals(uuid2, productCursor.getString(productCursor.getColumnIndex(Recipe.COLUMN_ID)));
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

        contentValues.put(Recipe.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe");

        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid)), contentValues);
        String pseudoUri = RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid);
        assertNotNull(uri);
        assertEquals(pseudoUri, uri.toString());

        Cursor cursor = mRecipeProvider.query(uri, Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_ID)));
        assertEquals("TestRecipe", cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_NAME)));
        resetDb();
    }


    public void testDeleteSingleRecipe() {

        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe");

        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid)), contentValues);

        int affectedRows = mRecipeProvider.delete(uri, null, null);
        assertEquals(1, affectedRows);
        Cursor cursor = mRecipeProvider.query(uri, Recipe.ALL_COLUMNS, null, null, null);
        assertNotNull(cursor);
        assertEquals(0, cursor.getCount());
    }

    public void testDeleteMultipleRecipes() {
        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");

        mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid)), contentValues);

        String uuid2 = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuid2);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe2");

        mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid2)), contentValues);

        int affectedRows = mRecipeProvider.delete(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), Recipe.COLUMN_NAME + " LIKE ?", new String[]{"%TestRecipe%"});

        assertEquals(2, affectedRows);

        contentValues.put(Recipe.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");

        mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid)), contentValues);

        contentValues.put(Recipe.COLUMN_ID, uuid2);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe2");

        mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid2)), contentValues);

        affectedRows = mRecipeProvider.delete(Uri.parse(RecipeProvider.MULTIPLE_RECIPE_CONTENT_URI), Recipe.COLUMN_NAME + " LIKE ?", new String[]{"%TestRecipe1%"});

        assertEquals(1, affectedRows);
    }

    public void testUpdateSingleRecipe() {
        ContentValues contentValues = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuid);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");

        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuid)), contentValues);

        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe2");
        int affectedRows = mRecipeProvider.update(uri, contentValues, null, null);

        assertEquals(1, affectedRows);

        Cursor cursor = mRecipeProvider.query(uri, Recipe.ALL_COLUMNS, null, null, null);
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();

        assertEquals("TestRecipe2", cursor.getString(cursor.getColumnIndex(Recipe.COLUMN_NAME)));
    }


    public void testInsertSingleIngredient() {

        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");
        // insert at begin a recipe
        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);


        String uuid = UUID.randomUUID().toString();
        uri = uri.buildUpon().appendPath("ingredient").appendPath(uuid).build();

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_AMOUNT, 1.0f);

        Uri ingredientUri = mRecipeProvider.insert(uri, ingredientValues);
        uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        assertNotNull(ingredientUri);
        assertEquals(true, uri.compareTo(ingredientUri) == 0);

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.ALL_COLUMNS, null, null, null);

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_ID)));

    }

    public void testQuerySingleIngredient() {
        String uuidRecipe = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{uuidRecipe, "TestRecipe1"});


        // insert at begin a recipe
        //Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);


        String uuid = UUID.randomUUID().toString();
        // Uri uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid, uuidProduct, uuidRecipe, String.valueOf(1.0f)});

        // Uri ingredientUri = mRecipeProvider.insert(uri,ingredientValues);
        Uri uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        // assertNotNull(ingredientUri);
        // assertEquals(true, uri.compareTo(ingredientUri) == 0);

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.ALL_COLUMNS, null, null, null);

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_ID)));
    }

    public void testQueryMultipleIngredients() {
        String uuidRecipe = UUID.randomUUID().toString();

        mDatabase.execSQL("INSERT INTO " + Recipe.TABLE_NAME + " VALUES (?,?)", new String[]{uuidRecipe, "TestRecipe1"});

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct1 = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct1);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct1");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct1)), contentValuesProduct);

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct2);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct2");

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct2)), contentValuesProduct);

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        // Uri uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid, uuidProduct1, uuidRecipe, String.valueOf(1.0f)});
        mDatabase.execSQL("INSERT INTO " + Ingredient.TABLE_NAME + " VALUES (?,?,?,?)", new String[]{uuid2, uuidProduct2, uuidRecipe, String.valueOf(2.0f)});

        Uri uri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe));

        Cursor cursor = mRecipeProvider.query(uri, Ingredient.ALL_COLUMNS, null, null, Ingredient.COLUMN_AMOUNT + " ASC");

        assertNotNull(cursor);
        // only one ingredient should be included
        assertEquals(2, cursor.getCount());

        cursor.moveToFirst();
        assertEquals(uuid, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_ID)));
        assertEquals(uuidProduct1, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_PRODUCT_ID)));
        assertTrue(cursor.moveToNext());
        assertEquals(uuid2, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_ID)));
        assertEquals(uuidProduct2, cursor.getString(cursor.getColumnIndex(Ingredient.COLUMN_PRODUCT_ID)));
    }

    public void testDeleteSingleIngredient() {

        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");
        // insert at begin a recipe
        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);

        String uuid = UUID.randomUUID().toString();
        uri = uri.buildUpon().appendPath("ingredient").appendPath(uuid).build();

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_AMOUNT, 1.0f);

        Uri ingredientUri = mRecipeProvider.insert(uri, ingredientValues);
        uri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));

        int affectedRows = mRecipeProvider.delete(ingredientUri, null, null);

        assertEquals(1, affectedRows);

    }

    public void testDeleteMultipleIngredient() {

        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");
        // insert at begin a recipe
        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("\\*", uuidProduct)), contentValuesProduct);

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct2);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct2");

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Uri uri1 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        Uri uri2 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid2));

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_AMOUNT, 1.0f);

        mRecipeProvider.insert(uri1, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_ID, uuid2);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct2);
        mRecipeProvider.insert(uri2, ingredientValues);

        uri = Uri.parse(RecipeProvider.MULTIPLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe));

        int affectedRows = mRecipeProvider.delete(uri, null, null);

        assertEquals(2, affectedRows);
    }

    public void testUpdateSingleIngredient() {
        ContentValues contentValues = new ContentValues();
        String uuidRecipe = UUID.randomUUID().toString();

        contentValues.put(Recipe.COLUMN_ID, uuidRecipe);
        contentValues.put(Recipe.COLUMN_NAME, "TestRecipe1");
        // insert at begin a recipe
        Uri uri = mRecipeProvider.insert(Uri.parse(RecipeProvider.SINGLE_RECIPE_CONTENT_URI.replace("*", uuidRecipe)), contentValues);

        ContentValues contentValuesProduct = new ContentValues();
        String uuidProduct = UUID.randomUUID().toString();
        String uuidProduct2 = UUID.randomUUID().toString();

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct");
        contentValuesProduct.put(Product.COLUMN_DEFAULT_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_STEP_AMOUNT, 0.5f);
        contentValuesProduct.put(Product.COLUMN_UNIT_ID, (String) null);

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);

        contentValuesProduct.put(Product.COLUMN_ID, uuidProduct2);
        contentValuesProduct.put(Product.COLUMN_NAME, "TestProduct2");

        mProductProvider.insert(Uri.parse(ProductProvider.SINGLE_PRODUCT_CONTENT_URI.replace("*", uuidProduct)), contentValuesProduct);

        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        Uri uri1 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        Uri uri2 = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid2));

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put(Ingredient.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct);
        ingredientValues.put(Ingredient.COLUMN_RECIPE_ID, uuidRecipe);
        ingredientValues.put(Ingredient.COLUMN_AMOUNT, 1.0f);

        mRecipeProvider.insert(uri1, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_ID, uuid2);
        ingredientValues.put(Ingredient.COLUMN_PRODUCT_ID, uuidProduct2);
        mRecipeProvider.insert(uri2, ingredientValues);

        ingredientValues.put(Ingredient.COLUMN_ID, uuid);
        ingredientValues.put(Ingredient.COLUMN_AMOUNT, 5.0f);

        Uri ingredientUri = Uri.parse(RecipeProvider.SINGLE_RECIPE_INGREDIENT_CONTENT_URI.replaceFirst("\\*", uuidRecipe).replaceFirst("\\*", uuid));
        int affectedRows = mRecipeProvider.update(ingredientUri,
                ingredientValues, null, null
        );

        assertEquals(1, affectedRows);

        Cursor cursor = mRecipeProvider.query(ingredientUri, Ingredient.ALL_COLUMNS, null,null, null);
        assertNotNull(cursor);
        cursor.moveToFirst();

        assertEquals(5.0f, cursor.getFloat(cursor.getColumnIndex(Ingredient.COLUMN_AMOUNT)));

    }
}
