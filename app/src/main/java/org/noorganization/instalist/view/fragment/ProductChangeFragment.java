/*
 * Copyright 2016 Tino Siegmund, Michael Wodniok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.noorganization.instalist.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.IProductController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.view.customview.AmountPicker;
import org.noorganization.instalist.view.event.ProductSelectMessage;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Fragment where the creation and the editing of an product is handled.
 * Created by TS on 28.04.2015.
 */
public class ProductChangeFragment extends DialogFragment {

    private static final String BUNDLE_KEY_PRODUCT_ID = "ProductId";

    private InputParamsHolder mInputParams;
    /**
     * used when product values should be rendered into view.
     */
    private Product mProduct;


    /**
     * Holds the input parameter views. Also delivers methods to retrieve the content of these
     * views.
     */
    private class InputParamsHolder {
        private EditText mProductName;
        private AmountPicker mProductAmount;
        private EditText mProductTags;
        private EditText mProductStep;
        private CheckBox mProductAdvancedSwitch;
        private LinearLayout mProductAdvancedContents;
        private Spinner mUnits;
        private Context mContext;
        private List<Unit> mUnitList;

        public InputParamsHolder(Dialog _dialog, View _parentView) {
            this.mContext = _dialog.getContext();
            initViews(_parentView);
        }

        /**
         * Checks if all needed editable fields are filled.
         * Marks an unfilled entry as not filled.
         *
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled() {
            return ViewUtils.checkEditTextIsFilled(mProductName);
        }

        /**
         * checks if the input matches the conventions.
         *
         * @return true if  all is fine, false when some value is curious.
         */
        public boolean isValid() {
            boolean returnValue = true;

            float amount = mProductAmount.getValue();
            if (amount <= 0.0f) {
                Toast.makeText(mContext, R.string.product_creation_fragment, Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

            return returnValue;
        }


        /**
         * Gets the product name.
         *
         * @return name of the product.
         */
        public String getProductName() {
            return mProductName.getText().toString();
        }

        /**
         * Creates a float value of the amount input.
         *
         * @return a float value of the amount input. if edittext is set the value of this, else 0.0f.
         */
        public float getProductAmount() {
            return mProductAmount.getValue();
        }

        /**
         * @return The step size for the product.
         */
        public float getProductStep() {
            return mProductAmount.getStep();
        }

        /**
         * Splits the tags in the given edittext. Separator is comma.
         *
         * @return a string array of extracted tags.
         */
        public String[] getTags() {
            String tagValue = mProductTags.getText().toString();
            LinkedList<String> rtn = new LinkedList<>(Arrays.asList(tagValue.split("\\s*,\\s*")));
            int last_size = rtn.size() + 1;
            while (last_size > rtn.size()) {
                last_size = rtn.size();
                rtn.remove("");
            }
            return rtn.toArray(new String[rtn.size()]);
        }

        public Unit getProductDefaultUnit() {
            int selectedPos = mUnits.getSelectedItemPosition();
            if (selectedPos == AdapterView.INVALID_POSITION) {
                selectedPos = 0;
            }
            return mUnitList.get(selectedPos);
        }

        /**
         * Assigns the context to the edit view elements in this class. (like EditText)
         */
        private void initViews(View _parentView) {

            mProductName = (EditText) _parentView.findViewById(R.id.product_details_product_name);
            mProductAmount = (AmountPicker) _parentView.findViewById(R.id.product_details_amount);
            mProductTags = (EditText) _parentView.findViewById(R.id.product_details_tag);
            mProductStep = (EditText) _parentView.findViewById(R.id.product_details_step);
            mProductAdvancedSwitch = (CheckBox) _parentView.findViewById(R.id.product_details_advanced);
            mProductAdvancedContents = (LinearLayout) _parentView.
                    findViewById(R.id.product_details_advanced_contents);
            mUnits = (Spinner) _parentView.findViewById(R.id.product_details_unit);

            mProductAdvancedContents.
                    setVisibility(mProductAdvancedSwitch.isChecked() ? View.VISIBLE : View.GONE);

            mProductAdvancedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mProductAdvancedContents.
                            setVisibility(mProductAdvancedSwitch.isChecked() ? View.VISIBLE : View.GONE);
                }
            });

            mProductStep.setKeyListener(ViewUtils.getNumberListener());
            mProductStep.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable _newText) {
                    float newStep = ViewUtils.parseFloatFromLocal(_newText.toString());
                    if (newStep > 0.0f) {
                        mProductAmount.setStep(newStep);
                    }
                }
            });

            mUnitList = ControllerFactory.getUnitController(GlobalApplication.getContext()).listAll(Unit.COLUMN.NAME, true);
            mUnitList.add(0, null);
            String[] displayUnitStrings = new String[mUnitList.size()];
            displayUnitStrings[0] = mContext.getString(R.string.no_unit);
            for (int currentUnitIndex = 1; currentUnitIndex < mUnitList.size(); currentUnitIndex++) {
                displayUnitStrings[currentUnitIndex] = mUnitList.get(currentUnitIndex).mName;
            }
            mUnits.setAdapter(new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line,
                    displayUnitStrings));

            fillViews();
        }

        private void fillViews() {
            Product currentProduct = ProductChangeFragment.this.mProduct;
            if (ProductChangeFragment.this.mProduct != null) {
                mProductName.setText(currentProduct.mName);
                mProductAmount.setValue(currentProduct.mDefaultAmount);

                // TODO use a cursor instead!
                List<TaggedProduct> taggedProductList = ControllerFactory.getProductController(mContext).findTaggedProductsByProduct(currentProduct);
                StringBuilder tagBuffer = new StringBuilder();
                if (!taggedProductList.isEmpty()) {
                    Iterator<TaggedProduct> taggedProductIterator = taggedProductList.iterator();
                    while (taggedProductIterator.hasNext()) {
                        tagBuffer.append(taggedProductIterator.next());
                        if (taggedProductIterator.hasNext()) {
                            tagBuffer.append(", ");
                        }
                    }
                }
                mProductTags.setText(tagBuffer);

                mProductStep.setText(ViewUtils.formatFloat(currentProduct.mStepAmount));
                mProductAmount.setStep(currentProduct.mStepAmount);
                if (mProduct.mUnit != null) {
                    mUnits.setSelection(mUnitList.indexOf(mProduct.mUnit));
                } else {
                    mUnits.setSelection(0);
                }
            }
        }
    }

    /**
     * saves the new product.
     *
     * @return true by success false by fail.
     */
    private boolean saveProduct() {
        IProductController productController = ControllerFactory.getProductController(GlobalApplication.getContext());

        if (mProduct == null) {
            mProduct = productController.createProduct(
                    mInputParams.getProductName(),
                    mInputParams.getProductDefaultUnit(),
                    mInputParams.getProductAmount(),
                    mInputParams.getProductStep()
            );

            if (mProduct == null) {
                return false;
            }

            Map<Product, Float> selectedProducts = new HashMap<>();
            selectedProducts.put(mProduct, mProduct.mDefaultAmount);
            EventBus.getDefault().post(new ProductSelectMessage(selectedProducts));
        } else {
            mProduct.mName = mInputParams.getProductName();
            mProduct.mDefaultAmount = mInputParams.getProductAmount();
            mProduct.mStepAmount = mInputParams.getProductStep();
            mProduct.mUnit = mInputParams.getProductDefaultUnit();

            Product savedProduct = productController.modifyProduct(mProduct);
            if (!mProduct.equals(savedProduct)) {
                return false;
            }
        }

        return saveTags(mProduct);
    }

    /**
     * Saves all the given tags.
     *
     * @param _Product the product with which they should be associated.
     * @return true if all goes well, false if something went wrong.
     */
    private boolean saveTags(Product _Product) {
        String[] tagArray = mInputParams.getTags();
        for (String currentTag : tagArray) {
            Tag tag = ControllerFactory.getTagController(GlobalApplication.getContext()).createTag(currentTag);
            if (tag == null) {
                tag = ControllerFactory.getTagController(GlobalApplication.getContext()).findByName(currentTag);
                if (tag == null) {
                    return false;
                }
            }
            if (ControllerFactory.getProductController(GlobalApplication.getContext()).addTagToProduct(_Product, tag) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates an instance of an ProductChangeFragment with the details of the product.
     *
     * @return the new instance of this fragment.
     */
    public static ProductChangeFragment newCreateInstance() {
        ProductChangeFragment fragment = new ProductChangeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates an instance of an ProductChangeFragment.
     *
     * @param _productId the id in the database of the product that should be edited.
     * @return the new instance of this fragment.
     */
    public static ProductChangeFragment newChangeInstance(String _productId) {
        ProductChangeFragment fragment = new ProductChangeFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_PRODUCT_ID, _productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(BUNDLE_KEY_PRODUCT_ID)) {
            mProduct = ControllerFactory.getProductController(GlobalApplication.getContext())
                    .findById(getArguments().getString(BUNDLE_KEY_PRODUCT_ID));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle _savedInstanceState) {

        int positveButtonString = (mProduct == null ? R.string.action_add : R.string.action_save);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(positveButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // stub. will be replaced when showing dialog.
            }
        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View inflatedDialogContents = inflater.inflate(R.layout.fragment_product_details, null);
        builder.setView(inflatedDialogContents);

        AlertDialog createdDialog = builder.create();
        mInputParams = new InputParamsHolder(createdDialog, inflatedDialogContents);
        createdDialog.setOnShowListener(new ClickListenerAssignee(createdDialog));
        return createdDialog;
    }

    private class OnAddListener implements View.OnClickListener {

        private AlertDialog mDialog;

        public OnAddListener(AlertDialog _dialog) {
            mDialog = _dialog;
        }

        @Override
        public void onClick(View _clicked) {
            if (!mInputParams.isFilled()) {
                return;
            }

            if (!mInputParams.isValid()) {
                return;
            }

            if (saveProduct()) {
                Toast.makeText(getActivity(), R.string.product_saved_okay, Toast.LENGTH_LONG).show();
                ViewUtils.removeFragment(getActivity(), ProductChangeFragment.this);
            } else {
                Toast.makeText(getActivity(), R.string.product_saved_fail, Toast.LENGTH_LONG).show();
            }

            mDialog.dismiss();
        }
    }

    private class ClickListenerAssignee implements DialogInterface.OnShowListener {

        private AlertDialog mDialog;

        public ClickListenerAssignee(AlertDialog _dialog) {
            mDialog = _dialog;
        }

        @Override
        public void onShow(DialogInterface dialog) {
            mDialog.getButton(DialogInterface.BUTTON_POSITIVE).
                    setOnClickListener(new OnAddListener(mDialog));
        }
    }
}