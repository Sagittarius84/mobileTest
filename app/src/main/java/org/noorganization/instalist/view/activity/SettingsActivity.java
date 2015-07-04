package org.noorganization.instalist.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.noorganization.instalist.view.fragment.settings.SettingsFragment;

/**
 * The Activity for setting Settings.
 * Created by TS on 04.07.2015.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
