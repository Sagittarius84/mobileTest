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
import org.noorganization.instalist.view.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Global application class.
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends SugarApp {

    private final static String LOG_TAG = GlobalApplication.class.getName();

    private static GlobalApplication mInstance;
    private DatabaseSeeder mDatabaseSeeder;

    private static ChangeHandler mChangeHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Create a handler and attach it to current thread.
        mChangeHandler = new ChangeHandler();

        // do this only in debug mode!
        // else it would destroy the database of a user and that would be the kill factor
        /*if (BuildConfig.DEBUG) {
            mDatabaseSeeder = DatabaseSeeder.getInstance();
            mDatabaseSeeder.startUp();
        }*/

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //mDatabaseSeeder.tearDown();
    }

    public static GlobalApplication getInstance() {
        return mInstance;
    }


    public static IChangeHandler getChangeHandler() {
        return mChangeHandler;
    }
}
