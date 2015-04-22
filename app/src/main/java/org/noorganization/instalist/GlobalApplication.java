package org.noorganization.instalist;

import android.app.Application;
import android.util.Log;

import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.database_seed.DatabaseSeeder;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;

import java.util.List;

/**
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends SugarApp {

    private final static String LOG_TAG = GlobalApplication.class.getName();

    private static GlobalApplication mInstance;
    private DatabaseSeeder mDatabaseSeeder;
    private String mCurrentShoppingListName;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mDatabaseSeeder = DatabaseSeeder.getInstance();
        mDatabaseSeeder.startUp();
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

        ShoppingList shoppingList   = Select.from(ShoppingList.class).where(Condition.prop(ShoppingList.getShoppingListPropertyName()).eq(listName)).first();

            List<ListEntry> entries = shoppingList.getEntries();
            Log.d(LOG_TAG, "Get list entries of list: " + shoppingList.mName + " number of elements: " + entries.size());


        return entries;
    }

    public List<ShoppingList> getShoppingListNames(){
        return Select.from(ShoppingList.class).list();
    }

    public String getCurrentShoppingListName() {
        return mCurrentShoppingListName;
    }

    public void setCurrentShoppingListName(String mCurrentShoppingListName) {
        this.mCurrentShoppingListName = mCurrentShoppingListName;
    }

}
