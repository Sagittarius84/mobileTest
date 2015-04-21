package org.noorganization.instalist;

import android.app.Application;

/**
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends Application {

    private static GlobalApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static GlobalApplication getInstance(){
        return mInstance;
    }


}
