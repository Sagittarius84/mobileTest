package org.noorganization.instalist.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
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

        ControllerFactory.getPluginController(this).searchPlugins();

        setSupportActionBar(mToolbar);
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
