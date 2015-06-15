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
 * Supports the onBackPressedEvent
 * Created by tinos_000 on 21.05.2015.
 */
public class BaseCustomFragment extends Fragment {

    public interface OnMainActivityCallback{
        void            changeFragment(Fragment _NewFragment);
        Toolbar         getToolbar();
        DrawerLayout    getDrawerLayout();
    }


    protected Activity mActivity;
    protected String mTitle;

    protected OnMainActivityCallback mMainActivityListener;

    @Override
    public void onAttach(Activity _Activity) {
        super.onAttach(_Activity);
        try {
            mMainActivityListener   = (OnMainActivityCallback) _Activity;
            mActivity               = _Activity;

/*
            if(mActivity instanceof MainShoppingListView) {
                MainShoppingListView mReferenceActivity = (MainShoppingListView) mActivity;
                mToolbar        =   mReferenceActivity.getToolbar();
                mDrawerLayout   =   mReferenceActivity.getDrawerLayout();
            }else{
                throw new IllegalStateException("The activity is not an instance of " +  MainShoppingListView.class.getName());
            }
             */

        } catch (ClassCastException e){
            throw new ClassCastException( _Activity.toString()
                    + " must implement OnMainActivityListener.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        return super.onCreateView(_Inflater, _Container, _SavedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTitle != null){
            mMainActivityListener.getToolbar().setTitle(mTitle);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTitle = mMainActivityListener.getToolbar().getTitle().toString();
    }

    /**
     * Locks the DrawerLayout as closed.
     */
    protected void lockDrawerLayoutClosed(){
        mMainActivityListener.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * Unlocks the DrawerLayout, so that open of the DrawerLayout is possible.
     */
    protected void unlockDrawerLayout(){
        mMainActivityListener.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Sets the title of the toolbar to the given text.
     * @param _Title the title that should be shown.
     */
    protected void setToolbarTitle(String _Title){
        mMainActivityListener.getToolbar().setTitle(_Title);
    }

    /**
     * Changes the current Fragment to the give fragment.
     * @param fragment The fragment to change to.
     */
    protected void changeFragment(Fragment fragment){
        mMainActivityListener.changeFragment(fragment);
    }

    /**
     * Calls the onBackPressed in parent activity.
     */
    protected void onBackPressed(){
        mActivity.onBackPressed();
    }
}
