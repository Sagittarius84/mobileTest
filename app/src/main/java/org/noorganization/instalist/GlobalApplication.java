package org.noorganization.instalist;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ListView;

import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.controller.database_seed.DatabaseSeeder;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.ShoppingList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends SugarApp {

    private final static String LOG_TAG = GlobalApplication.class.getName();

    private static GlobalApplication mInstance;
    private DatabaseSeeder mDatabaseSeeder;

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
}
