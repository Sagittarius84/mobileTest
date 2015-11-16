package org.noorganization.instalist.presenter.touchlistener.sidebar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ShoppingList;

// TODO: delete?

/**
 * Handles the viewchange from textview to edittext.
 */
public class OnShoppingListLongClickListener implements View.OnLongClickListener {

    private String mShoppingListId;

    public OnShoppingListLongClickListener(String _ShoppingListId) {
        mShoppingListId = _ShoppingListId;
    }

    @Override
    public boolean onLongClick(View _View) {

        EditText     editText;
        ShoppingList shoppingList;
        ViewSwitcher viewSwitcher;
        ImageView    cancelView, submitView;

        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

        viewSwitcher = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
        editText = (EditText) _View.findViewById(R.id.expandable_list_view_list_edit_name);

        shoppingList = ControllerFactory.getListController(GlobalApplication.getContext()).getListById(mShoppingListId);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithChildData(GlobalApplication.getContext(),viewSwitcher, editText, mShoppingListId));

        editText.setText(shoppingList.mName);
        viewSwitcher.showNext();
        return false;
    }
}