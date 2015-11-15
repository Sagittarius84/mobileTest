package org.noorganization.instalist.presenter.touchlistener.sidebar;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.utils.ViewUtils;

public class OnSubmitClickListenerWithChildData implements View.OnClickListener {

    private ViewSwitcher mViewSwitcher;
    private EditText     mNameEditText;
    private String       mShoppingListId;

    private IListController mListController;

    public OnSubmitClickListenerWithChildData(Context _context, ViewSwitcher _ViewSwitcher,
                                              EditText _NameEditText, String _ShoppingListId) {
        mViewSwitcher = _ViewSwitcher;
        mNameEditText = _NameEditText;
        mShoppingListId = _ShoppingListId;
        mListController = ControllerFactory.getListController(_context);
    }

    @Override
    public void onClick(View _View) {
        if (! ViewUtils.checkEditTextIsFilled(mNameEditText)) {
            return;
        }

        ShoppingList oldShoppingList, newShoppingList;
        String       insertedText;
        Context  context = _View.getContext();

        insertedText = mNameEditText.getText().toString();
        oldShoppingList = mListController.getListById(mShoppingListId);
        newShoppingList = mListController.renameList(oldShoppingList, insertedText);

        if (newShoppingList == null) {
            Toast.makeText(_View.getContext(), context.getString(R.string.shopping_list_not_found), Toast.LENGTH_SHORT).show();
            return;
        } else if (newShoppingList.equals(oldShoppingList)) {
           /* if (newShoppingList.mName.compareTo(insertedText) != 0) {

                Toast.makeText(context, context.getString(R.string.list_exists), Toast.LENGTH_SHORT).show();
                mNameEditText.setError(context.getString(R.string.list_exists));
                return;
            }*/
        }

        ViewUtils.removeSoftKeyBoard(context, mNameEditText);
        // TODO: remove this when callback for this kind is there.
        ((IBaseActivity) context).updateList(newShoppingList);
        mViewSwitcher.showNext();
    }
}