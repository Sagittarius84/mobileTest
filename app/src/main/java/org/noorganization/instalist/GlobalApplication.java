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

package org.noorganization.instalist;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.event.ListItemChangedMessage;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.view.event.ActivityStateMessage;
import org.noorganization.instalist.view.event.ProductSelectMessage;
import org.noorganization.instalist.view.event.ShoppingListOverviewFragmentActiveEvent;
import org.noorganization.instalist.view.event.ShoppingListSelectedMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Global application class.
 * Created by TS on 21.04.2015.
 */
public class GlobalApplication extends Application {

    private final static String LOG_TAG = GlobalApplication.class.getName();

    private static GlobalApplication mInstance;
    private ShoppingList mCurrentShoppingList;

    private boolean mBufferItemChangedMessages;
    private boolean mHandlingProductSelectedMessages;
    private List<ListItemChangedMessage> mBufferedMessages;

    public static GlobalApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        EventBus.getDefault().register(this);
        mBufferedMessages = new LinkedList<>();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void onEvent(ActivityStateMessage _message) {
        mHandlingProductSelectedMessages = (_message.mState == ActivityStateMessage.State.RESUMED);
    }

    public void onEvent(ShoppingListSelectedMessage _selectedShoppingList) {
        mCurrentShoppingList = _selectedShoppingList.mShoppingList;
    }

    public void onEvent(ShoppingListOverviewFragmentActiveEvent _event) {
        mBufferItemChangedMessages = !_event.mActive;
        if (!mBufferItemChangedMessages) {
            for (ListItemChangedMessage message : mBufferedMessages)
                EventBus.getDefault().post(message);

            mBufferedMessages.clear();
        }
    }

    public void onEvent(ListItemChangedMessage _message) {
        if (mBufferItemChangedMessages) {
            mBufferedMessages.add(_message);
        }
    }

    public void onEvent(ProductSelectMessage _message) {
        if (mCurrentShoppingList == null) {
            Toast.makeText(getContext(), R.string.abc_no_shopping_list_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mHandlingProductSelectedMessages) {
            Map<Product, Float> productAmounts = _message.mProducts;
            IListController controller = ControllerFactory.getListController(getContext());
            for (Product currentProduct : productAmounts.keySet()) {
                controller.addOrChangeItem(mCurrentShoppingList, currentProduct,
                                           productAmounts.get(currentProduct), true);
            }
        }
    }

}
