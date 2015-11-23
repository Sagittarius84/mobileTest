package org.noorganization.instalist.presenter.touchlistener.sidebar;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.ICategoryController;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.fragment.ShoppingListOverviewFragment;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.utils.ViewUtils;

/**
 * Used to process various add list processes to categories.
 * Created by TS on 27.06.2015.
 */
public class OnShoppingListAddClickListener implements View.OnClickListener {

    /**
     * The Category Id.
     */
    private String mCategoryId;

    /**
     * The EditText where the new ShoppingList name is placed.
     */
    private EditText mNewNameEditText;

    private IListController mListController;
    private ICategoryController mCategoryController;

    /**
     * Constructor of OnShoppingListAddClickListener
     * @param _CategoryId the CategoryId where the list should be added. If in PlainList Mode then use the default category.
     */
    public OnShoppingListAddClickListener(Context _context, String _CategoryId, EditText _NewNameEditText) {
        mCategoryId = _CategoryId;
        mNewNameEditText = _NewNameEditText;
        mListController = ControllerFactory.getListController(_context);
        mCategoryController = ControllerFactory.getCategoryController(_context);
    }

    @Override
    public void onClick(View _View) {
        Context context = _View.getContext();

        String listName = ViewUtils.validateAndGetResultEditText(context, mNewNameEditText);
        if (listName == null) {
            return;
        }

        ShoppingList shoppingList = mListController.addList(listName);
        if (shoppingList == null) {
            mNewNameEditText.setError(context.getResources().getString(R.string.list_exists));
            return;
        }
        Category category = mCategoryController.getCategoryByID(mCategoryId);
        shoppingList = mListController.moveToCategory(shoppingList, category);

        if(shoppingList == null){
            mNewNameEditText.setError(context.getResources().getString(R.string.list_to_category_failed));
            return;
        }

        ViewUtils.removeSoftKeyBoard(_View.getContext(), mNewNameEditText);
        _View.setVisibility(View.GONE);
        mNewNameEditText.clearFocus();
        // clear the field
        mNewNameEditText.setText("");
        // ((IBaseActivity) context).addList(shoppingList);

        ((IBaseActivity) context).changeFragment(ShoppingListOverviewFragment.newInstance(shoppingList.mUUID));
    }
}
