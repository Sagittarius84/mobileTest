package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.noorganization.instalist.view.MainShoppingListView;

/**
 * Designed to hold the toolbar, drawerlayout and a activity that must be of type MainShoppingListView.
 * Created by tinos_000 on 21.05.2015.
 */
public class BaseCustomFragment extends Fragment {

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected Activity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        if(mActivity instanceof MainShoppingListView) {
            mToolbar = ((MainShoppingListView) mActivity).getToolbar();
            mDrawerLayout = ((MainShoppingListView) mActivity).getDrawerLayout();
        }else{
            throw new IllegalStateException("The activity is not an instance of " +  MainShoppingListView.class.getName());
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Locks the DrawerLayout as closed.
     */
    protected void lockDrawerLayoutClosed(){
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Unlocks the DrawerLayout, so that open of the DrawerLayout is possible.
     */
    protected void unlockDrawerLayout(){
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Sets the title of the toolbar to the given text.
     * @param _Title the title that should be shown.
     */
    protected void setToolbarTitle(String _Title){
        mToolbar.setTitle(_Title);
    }
}
