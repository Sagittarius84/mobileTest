package org.noorganization.instalist.presenter;

/**
 * This Interface enables the view to search for extenal plugins that need to be registered in this
 * app at least for settings. The intended use is more overview, what belongs to this app.
 * Created by damihe on 06.01.16.
 */
public interface IPluginController {

    /**
     * Starts a search for the Plugins. This Process is asynchronous and it's return value will be
     * delivered via the bus.
     */
    void searchPlugins();
}
