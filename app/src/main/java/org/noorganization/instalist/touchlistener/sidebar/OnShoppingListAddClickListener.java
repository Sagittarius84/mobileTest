package org.noorganization.instalist.touchlistener.sidebar;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.utils.ViewUtils;

/**
 * Used to process various add list processes to categories.
 * Created by TS on 27.06.2015.
 */
public class OnShoppingListAddClickListener implements View.OnClickListener {

    /**
     * The Category Id.
     */
    private long mCategoryId;

    /**
     * The EditText where the new ShoppingList name is placed.
     */
    private EditText mNewNameEditText;

    /**
     * Constructor of OnShoppingListAddClickListener
     * @param _CategoryId the CategoryId where the list should be added. If in PlainList Mode then use the default category.
     */
    public OnShoppingListAddClickListener(long _CategoryId, EditText _NewNameEditText) {
        mCategoryId = _CategoryId;
        mNewNameEditText = _NewNameEditText;
    }

    @Override
    public void onClick(View _View) {
        Context context = _View.getContext();

        String listName = ViewUtils.validateAndGetResultEditText(context, mNewNameEditText);
        if (listName == null) {
            return;
        }

        ShoppingList shoppingList = ControllerFactory.getListController().addList(listName);
        if (shoppingList == null) {
            mNewNameEditText.setError(context.getResources().getString(R.string.list_exists));
            return;
        }
        Category category = Category.findById(Category.class, mCategoryId);
        shoppingList = ControllerFactory.getListController().moveToCategory(shoppingList, category);

        if(shoppingList == null){
            mNewNameEditText.setError(context.getResources().getString(R.string.list_to_category_failed));
            ControllerFactory.getListController().removeList(shoppingList);
            return;
        }

        ViewUtils.removeSoftKeyBoard(_View.getContext(), mNewNameEditText);
        _View.setVisibility(View.GONE);
        mNewNameEditText.clearFocus();
        // clear the field
        mNewNameEditText.setText("");
        ((IBaseActivity) context).addList(shoppingList);

        ((IBaseActivity) context).changeFragment(ShoppingListOverviewFragment.newInstance(shoppingList.mName));
    }
}
