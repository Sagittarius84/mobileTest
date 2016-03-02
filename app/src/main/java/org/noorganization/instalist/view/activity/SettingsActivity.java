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

package org.noorganization.instalist.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.implementation.PluginControllerFactory;
import org.noorganization.instalist.view.fragment.settings.SettingsFragment;

/**
 * The Activity for setting Settings.
 * Created by TS on 04.07.2015.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_w_actionbar);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();

        // init and setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        PluginControllerFactory.getPluginController(this).searchPlugins();

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mToolbar.setNavigationOnClickListener(null);
    }

}
