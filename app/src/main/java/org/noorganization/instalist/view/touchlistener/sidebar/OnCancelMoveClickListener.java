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
