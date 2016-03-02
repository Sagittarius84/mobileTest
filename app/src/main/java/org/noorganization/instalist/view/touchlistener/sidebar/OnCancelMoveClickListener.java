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

package org.noorganization.instalist.view.touchlistener.sidebar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

/**
 * OnCancelMoveClickListener handles cancel of a move of a category of a list.
 * Created by TS on 21.06.2015.
 */
public class OnCancelMoveClickListener implements View.OnClickListener{

    private LinearLayout mMoveCategoryLayout;
    private ViewSwitcher mMainView;

    /**
     * Constructor of OnCancelMoveClickListener.
     * @param _MoveCategoryLayout The container of the view where the changeaction is hold.
     * @param _MainView The container of the ViewSwitcher where the default view is defined.
     */
    public OnCancelMoveClickListener( LinearLayout _MoveCategoryLayout, ViewSwitcher _MainView){
        mMoveCategoryLayout = _MoveCategoryLayout;
        mMainView = _MainView;
    }

    @Override
    public void onClick(View v) {
        mMoveCategoryLayout.setVisibility(View.GONE);
        mMainView.setVisibility(View.VISIBLE);
    }
}
