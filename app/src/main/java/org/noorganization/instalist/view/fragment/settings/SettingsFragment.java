package org.noorganization.instalist.view.fragment.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.noorganization.instalist.R;

/**
 * The settings fragment. Use Fragments instead of Activity to support fragment design and
 * this methods are recommended by Google.
 * Created by TS on 04.07.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
