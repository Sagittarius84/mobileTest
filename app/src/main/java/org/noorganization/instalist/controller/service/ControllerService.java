package org.noorganization.instalist.controller.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * The Service to handle all write and update actions related to the database.
 * This will be executed in the service thread to prevent the UI from struggeling.
 * Created by Tino on 16.10.2015.
 */
public class ControllerService extends IntentService {

    /**
     * Default constructor of {@link ControllerService}.
     * @param _Name the name of the service.
     */
    public ControllerService(String _Name) {
        super(_Name);
    }

    @Override
    protected void onHandleIntent(Intent _Intent) {

    }

}
