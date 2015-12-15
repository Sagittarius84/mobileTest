package org.noorganization.instalist.view.fragment.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

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


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if(preference.getKey().equals("open_source_licenses")){
            WebView view = (WebView) LayoutInflater.from(this.getActivity()).inflate(R.layout.licenses, null);
            view.loadUrl("file:///android_asset/open_source_licenses.html");
            AlertDialog mAlertDialog = new AlertDialog.Builder(this.getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle(getString(R.string.open_source_licenses))
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
