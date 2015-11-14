package org.noorganization.instalist;

import android.content.Context;

import com.orm.SugarApp;

import org.noorganization.instalist.controller.database_seed.DatabaseSeeder;

/**
 * Global application class.
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

    public static Context getContext(){
        return getInstance().getApplicationContext();
    }
}
