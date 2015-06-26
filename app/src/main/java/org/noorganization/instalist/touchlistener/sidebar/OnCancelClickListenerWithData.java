package org.noorganization.instalist.touchlistener.sidebar;

import android.view.View;
import android.widget.ViewSwitcher;

/**
 * Handles the click of the cancel action.
 * Created by TS on 20.06.2015.
 */
public class OnCancelClickListenerWithData implements View.OnClickListener {

    private ViewSwitcher mViewSwitcher;

    public OnCancelClickListenerWithData(ViewSwitcher _ViewSwitcher) {
        mViewSwitcher = _ViewSwitcher;
    }

    @Override
    public void onClick(View v) {
        mViewSwitcher.showNext();
    }
}