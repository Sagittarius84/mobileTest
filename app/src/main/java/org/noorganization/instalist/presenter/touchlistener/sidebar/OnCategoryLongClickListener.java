package org.noorganization.instalist.presenter.touchlistener.sidebar;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.presenter.listadapter.ExpandableCategoryItemListAdapter;

// TODO: delete?

/**
 * Created by TS on 20.06.2015.
 */
public class OnCategoryLongClickListener implements View.OnLongClickListener {

    private ExpandableCategoryItemListAdapter mAdapter;
    private long                              mCategoryId;

    public OnCategoryLongClickListener(long _CategoryId, ExpandableCategoryItemListAdapter _Adapter) {
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

        cancelView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_cancel);
        submitView = (ImageView) _View.findViewById(R.id.expandable_list_view_edit_submit);

        category = (Category) mAdapter.findCategoryById(mCategoryId);

        viewSwitcher = (ViewSwitcher) _View.findViewById(R.id.expandable_list_view_view_switcher);
        editText = (EditText) _View.findViewById(R.id.expandable_list_view_category_name_edit);

        cancelView.setOnClickListener(new OnCancelClickListenerWithData(viewSwitcher));
        submitView.setOnClickListener(new OnSubmitClickListenerWithParentData(viewSwitcher, editText, mCategoryId, mAdapter));

        editText.setText(category.mName);
        viewSwitcher.showNext();
        return true;
    }

}