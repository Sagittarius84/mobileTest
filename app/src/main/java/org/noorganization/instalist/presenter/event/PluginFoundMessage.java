package org.noorganization.instalist.presenter.event;

/**
 * Event-Message for notifying about found plugins.
 * Created by damihe on 06.01.16.
 */
public class PluginFoundMessage {
    public String mName;
    public String mSettingsActivity;
    public String mPackage;

    public PluginFoundMessage(String _name, String _settingsActivity, String _package) {
        mName = _name;
        mSettingsActivity = _settingsActivity;
        mPackage = _package;
    }
}
