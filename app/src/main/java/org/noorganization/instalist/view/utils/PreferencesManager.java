/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * General Preference Manager for all preferences. Singleton, first call
 * {@link #initializeInstance(Context)} and then {@Link #getInstance()}.
 * Created by tinos_000 on 23.06.2015.
 */
public class PreferencesManager {

    private static String PREFERENCES_NAME = "shoppinglist_settings";


    // --------------------------------------------------------------------------------
    //region Keys
    /**
     * The key for the default category name. Used to store uncategorized ListEntries in this.
     */
    public static String KEY_DEFAULT_CATEGORY_ID = "default_category_id";
    //endregion

    // --------------------------------------------------------------------------------
    // singleton
    private static PreferencesManager mInstance;

    private SharedPreferences mSharedPreferences;

    /**
     * The initializer of an instance. Use this before anything else.
     *
     * @param _Context The context of the Activity where the preferences are saved.
     */
    public static void initializeInstance(Context _Context) {
        if (mInstance == null) {
            mInstance = new PreferencesManager(_Context);
        }
    }

    /**
     * The Constructor of PreferencesManager. Assigns the context.
     *
     * @param _Context the Context on which the preferences should be defined.
     */
    private PreferencesManager(Context _Context) {
        mSharedPreferences = _Context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Retrieve the instance of PreferencesManager.
     *
     * @return the instace of PreferencesManager.
     */
    public static PreferencesManager getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Instance of PreferencesManager Singleton" + "" +
                    "was not initializedl. First call initializeInstance.");
        }
        return mInstance;
    }

    /**
     * Persists the given key value pair.
     *
     * @param _Key   the key of the value.
     * @param _Value the value that is assigned to the key.
     */
    public void setValue(String _Key, long _Value) {
        mSharedPreferences.edit()
                .putLong(_Key, _Value)
                .apply();
    }

    /**
     * Persists the given key value pair.
     *
     * @param _Key   the key of the value.
     * @param _Value the value that is assigned to the key.
     */
    public void setValue(String _Key, int _Value){
        mSharedPreferences.edit()
                .putInt(_Key, _Value)
                .apply();
    }

    /**
     * Persists the given key value pair.
     *
     * @param _Key   the key of the value.
     * @param _Value the value that is assigned to the key.
     */
    public void setValue(String _Key, String _Value) {
        mSharedPreferences.edit()
                .putString(_Key, _Value)
                .apply();
    }

    /**
     * Retrieves the long value of the given key.
     *
     * @param _Key the key for which the value should be returned.
     * @return if key exists the corresponding value of that key,  else -1L.
     */
    public long getLongValue(String _Key) {
        return mSharedPreferences.getLong(_Key, -1L);
    }

    /**
     * Retrieves the int value of the given key.
     *
     * @param _Key the key for which the value should be returned.
     * @return if key exists the corresponding value of that key,  else -1.
     */
    public int getIntValue(String _Key) {
        return mSharedPreferences.getInt(_Key, -1);
    }

    /**
     * Retrieves the String value of the given key.
     *
     * @param _Key the key for which the value should be returned.
     * @return if key exists the corresponding value of that key,  else null.
     */
    public String getStringValue(String _Key) {
        return mSharedPreferences.getString(_Key, null);
    }

    /**
     * Retrieves the assigned sharedPreferences object. Use it for special tasks.
     *
     * @return the sharedpreferences hold by the singleton.
     */
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

}
