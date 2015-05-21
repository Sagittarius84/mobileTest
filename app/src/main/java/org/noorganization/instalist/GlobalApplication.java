package org.noorganization.instalist;

import android.util.Log;

import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.IProductController;
import org.noorganization.instalist.controller.database_seed.DatabaseSeeder;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.controller.implementation.ProductController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.ChangeHandler;
import org.noorganization.instalist.view.IChangeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends SugarApp {

    private final static String LOG_TAG = GlobalApplication.class.getName();

    private static GlobalApplication mInstance;
    private DatabaseSeeder mDatabaseSeeder;

    private IListController mListController;
    private IProductController mProductController;

    private static ChangeHandler mChangeHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mListController = ControllerFactory.getListController();
        mProductController = new ProductController();

        // Create a handler and attach it to current thread.
        mChangeHandler = new ChangeHandler();

        // do this only in debug mode!
        // else it would destroy the database of a user and that would be the kill factor
        if(BuildConfig.DEBUG) {
            mDatabaseSeeder = DatabaseSeeder.getInstance();
            mDatabaseSeeder.startUp();
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDatabaseSeeder.tearDown();
    }

    public static GlobalApplication getInstance(){
        return mInstance;
    }

    public List<ListEntry> getListEntries(String listName){
        ShoppingList shoppingList = Select.from(ShoppingList.class).where(Condition.prop(ShoppingList.LIST_NAME_ATTR).eq(listName)).first();
        List<ListEntry> entries = shoppingList.getEntries();
        Log.d(LOG_TAG, "Get list entries of list: " + shoppingList.mName + " number of elements: " + entries.size());
        return entries;
    }

    public List<String> getShoppingListNames(){
        List<ShoppingList> shoppingLists = Select.from(ShoppingList.class).list();
        List<String> shoppingListNames = new ArrayList<>();

        for (ShoppingList shoppingList : shoppingLists) {
            // fill navbar with some sample data
            shoppingListNames.add(shoppingList.mName);
        }

        return shoppingListNames;
    }

    /**
     *
     * @return global instance of IListController implmentation.
     */
    public IListController getListController(){
        return mListController;
    }

    /**
     *
     * @return an instance of a productController.
     */
    public IProductController getProductController(){
        return mProductController;
    }

    public static IChangeHandler getChangeHandler() {
        return mChangeHandler;
    }
}
