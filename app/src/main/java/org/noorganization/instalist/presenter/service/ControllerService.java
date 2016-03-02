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

package org.noorganization.instalist.presenter.service;

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
