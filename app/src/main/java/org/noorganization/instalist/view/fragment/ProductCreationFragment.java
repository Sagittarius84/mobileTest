package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ProductController;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;

import java.util.List;

/**
 * Created by TS on 28.04.2015.
 */
public class ProductCreationFragment extends Fragment {


    private final static class InputParamsHolder{
        private EditText mProductNameEditText;
        private EditText mProductAmountEditText;
        private EditText mProductTagsEditText;

        public InputParamsHolder(View view){
            mProductNameEditText    = (EditText) view.findViewById(R.id.product_details_product_name_edittext);
            mProductAmountEditText  = (EditText) view.findViewById(R.id.product_details_amount_edittext);
            mProductTagsEditText    = (EditText) view.findViewById(R.id.product_details_tag_edittext);
        }

        public boolean isValid(){
            return false;
        }

        public String getProductName(){
            return mProductNameEditText.getText().toString();
        }

        public float getProductAmount(){
            return Float.valueOf(mProductAmountEditText.getText().toString());
        }

        public String[] getTags(){
            String tag = mProductTagsEditText.getText().toString();
            return tag.split(",");
        }

    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Product product = ProductController.getInstance().createProduct(
                    mInputParams.getProductName(),
                    null,
                    mInputParams.getProductAmount(),
                    0.1f
            );
            String[] tagArray = mInputParams.getTags();
            for(int Index = 0; Index < tagArray.length; ++ Index){
                Tag tag = new Tag(tagArray[Index]);
                ProductController.getInstance().addTagToProduct(product, tag);
            }
        }
    };

    private InputParamsHolder mInputParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        mInputParams = new InputParamsHolder(view);
        Button addButton = (Button) view.findViewById(R.id.product_details_action_button_new_or_update);
        addButton.setOnClickListener(mClickListener);
        return view;
    }
}
