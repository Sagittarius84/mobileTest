package org.noorganization.instalist.view.touchlistener.sidebar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.ICategoryController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.view.listadapter.ExpandableCategoryItemListAdapter;

// TODO: delete?

/**
 * Created by TS on 20.06.2015.
 */
public class OnCategoryLongClickListener implements View.OnLongClickListener {

    private ExpandableCategoryItemListAdapter mAdapter;
    private String mCategoryId;

    public OnCategoryLongClickListener(String _CategoryId, ExpandableCategoryItemListAdapter _Adapter) {
        mCategoryId = _CategoryId;
        mAdapter = _Adapter;
    }

    @Override
    public boolean onLongClick(View _View) {
        if (_View == null) return false;

        Category     category;
        EditText     editText;
        ImageView    cancelView, submitView;
        ViewSwitcher viewSwitcher;
        ICategoryController categoryController = ControllerFactory.getCategoryController(GlobalApplication.getContext());

        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

        category = categoryController.getCategoryByID(mCategoryId);

        viewSwitcher = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
        editText = (EditText) _View.findViewById(R.id.expandable_list_view_category_name_edit);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithParentData(GlobalApplication.getContext(), viewSwitcher, editText, mCategoryId, mAdapter));

        editText.setText(category.mName);
        viewSwitcher.showNext();
        return true;
    }

}