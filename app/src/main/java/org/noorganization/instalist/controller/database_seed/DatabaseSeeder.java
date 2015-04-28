package org.noorganization.instalist.controller.database_seed;

import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ListController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fills the database with some sample data.
 * Created by tinos_000 on 21.04.2015.
 */
public class DatabaseSeeder {

    private final static String LOG_TAG = DatabaseSeeder.class.getName();

    private static DatabaseSeeder mInstance;

    private final String    SAMPLE_TAG              = "SAMPLE";
    private final int       PRODUCT_TEST_DATA_SIZE  = 100;
    private final long      PRODUCT_LIST_SEED       = 324982340237840L;

    private IListController mListController;

    public static DatabaseSeeder getInstance(){

        if(mInstance == null)
            mInstance = new DatabaseSeeder();

        return mInstance;
    }

    // -----------------------------------------------------------------------
    // PRIVATE AREA
    // -----------------------------------------------------------------------

    private DatabaseSeeder(){
        mListController = ListController.getInstance();
    }

    /**
     * Fill the database with some sample data.
     */
    public void startUp(){

        List<ShoppingList>  shoppingList  = ShoppingList.listAll(ShoppingList.class);

        for(ShoppingList list : shoppingList)
        {
            List<ListEntry> listEntries = Select.from(ListEntry.class).where(
                    Condition.prop("m_list").eq(list.getId())).list();
            for(ListEntry listEntry : listEntries) {
               if(mListController.removeItem(listEntry)){
                   Log.d(LOG_TAG, "Deleted item.");
               }else{
                   Log.d(LOG_TAG, "Deletion of item failed.");
               }
            }

            if(mListController.removeList(list)){
                Log.d(LOG_TAG, "Delete list successful.");
            } else{
                Log.d(LOG_TAG, "Delete list failed.");
            }
        }

        Random rand = new Random(PRODUCT_LIST_SEED);
        String[]        listNames           = new String[]{SAMPLE_TAG.concat("_Home"), SAMPLE_TAG.concat("_Work")};
        String[]        listProductNames    = new String[]{"Sugar", "Beer", "Cheese", "Ham", "Nails", "Grenade Apple Juice"};
        String[]        listUnitNames       = new String[]{"g", "kg", "ml", "l", "hl", "pfund"};
        List<Product>       productList     = new ArrayList<>();
        List<ShoppingList>  shoppingLists   = new ArrayList<>();

        // add new lists
        for(String listName : listNames) {
            shoppingLists.add(mListController.addList(listName));
            Log.d(LOG_TAG, "List name: " + listName);
            //shoppingLists[counter].save();
        }


        for(int Index = 0; Index < PRODUCT_TEST_DATA_SIZE; ++ Index){
            String  productName;
            Product singleProduct;
            Unit    singleUnit;

            productName     = listProductNames[rand.nextInt(listProductNames.length)].concat("_" + String.valueOf(Index));
           // singleUnit      = new Unit(listUnitNames[rand.nextInt(listUnitNames.length)]);
           // singleUnit.save();

            singleProduct   = new Product(productName, null, 1.0f, 1.0f);

            singleProduct.save();
            productList.add(singleProduct);
        }

        for(Product product : productList){
            Log.d(LOG_TAG, "Add Product: " + product.mName);

            //mListController.addOrChangeItem(shoppingLists[rand.nextInt(shoppingLists.length)], product, rand.nextFloat());
            ShoppingList list = shoppingLists.get(rand.nextInt(shoppingLists.size()));
            Log.d(LOG_TAG, "List name: " + list.mName);
            ListEntry listEntry = new ListEntry(list, product, rand.nextFloat() * 100.0f);
            listEntry.save();
        }
    }

    /**
     * Delete all sample data from database.
     */
    public void tearDown(){
        List<ShoppingList>  shoppingLists  = ShoppingList.listAll(ShoppingList.class);
        List<ListEntry>     listEntries    = ListEntry.listAll(ListEntry.class);

        for(ListEntry listEntry : listEntries)
            mListController.removeItem(listEntry);

        for( ShoppingList shoppingList : shoppingLists)
            mListController.removeList(shoppingList);


    }
}
