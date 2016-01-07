package org.noorganization.instalist.view.fragment.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.event.PluginFoundMessage;

import de.greenrobot.event.EventBus;

/**
 * The settings fragment. Use Fragments instead of Activity to support fragment design and
 * this methods are recommended by Google.
 * Created by TS on 04.07.2015.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    private LayoutInflater mInflater;
    private ViewGroup mViewContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference.getKey() != null && preference.getKey().equals("open_source_licenses")) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mViewContainer = container;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(PluginFoundMessage _msg) {
        if (mInflater == null || mViewContainer == null) {
            Log.e(LOG_TAG, "onEvent: Faster than light, onCreateView was not called yet.");
            return;
        }

        PreferenceGroup preferenceGroup = new PreferenceCategory(mViewContainer.getContext());
        preferenceGroup.setTitle(_msg.mName);


        View prefView = preferenceGroup.getView(null, mViewContainer);

        ViewGroup viewGroup = (ViewGroup) mViewContainer.getChildAt(0);
        viewGroup.addView(prefView, viewGroup.getChildCount());

    }


}
