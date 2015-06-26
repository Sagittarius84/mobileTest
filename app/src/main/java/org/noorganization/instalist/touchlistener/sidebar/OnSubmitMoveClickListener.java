package org.noorganization.instalist.touchlistener.sidebar;

import android.content.Context;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;

/**
 * OnSubmitMoveClickListener handles the move action when a list should be moved to another category.
 * Created by TS on 21.06.2015.
 */
public class OnSubmitMoveClickListener implements View.OnClickListener {
    private LinearLayout mMoveCategoryLayout;
    private ViewSwitcher mMainView;
    private Spinner mSpinner;
    private ShoppingList mShoppingList;

    /**
     * Constructor of OnSubmitMoveClickListener.
     * @param _MoveCategoryLayout The container of the view where the changeaction is hold.
     * @param _MainView The container of the ViewSwitcher where the default view is defined.
     * @param _Spinner The Spinner object where a category can be cohoosen.
     * @param _ShoppingList  The ShoppingList that should be moved.
     */
    public OnSubmitMoveClickListener( LinearLayout _MoveCategoryLayout, ViewSwitcher _MainView,
                                      Spinner _Spinner, ShoppingList _ShoppingList){
        mMoveCategoryLayout = _MoveCategoryLayout;
        mMainView = _MainView;
        mSpinner = _Spinner;
        mShoppingList = _ShoppingList;
    }

    @Override
    public void onClick(View _View) {
        Context context = _View.getContext();

        Category category = (Category) mSpinner.getSelectedItem();
        mShoppingList = ControllerFactory.getListController().moveToCategory(mShoppingList, category);
        if(mShoppingList == null){
            Toast.makeText(context, context.getString(R.string.change_of_category_of_list_failed), Toast.LENGTH_SHORT).show();
        }
        mMoveCategoryLayout.setVisibility(View.GONE);
        mMainView.setVisibility(View.VISIBLE);
    }
}
