package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.controller.implementation.ProductController;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Tag;

/**
 * Created by TS on 28.04.2015.
 */
public class ProductCreationFragment extends Fragment {

    private InputParamsHolder mInputParams;

    /**
     * Holds the input parameter views. Also delivers methods to retrieve the content of these
     * views.
     */
    private final static class InputParamsHolder{
        private EditText mProductNameEditText;
        private EditText mProductAmountEditText;
        private EditText mProductTagsEditText;
        private View     mView;

        public InputParamsHolder(View view){
            assignContextToEditViews(view);
            this.mView              = view;
        }

        /**
         *
         * @param view          the view of the calling element.
         * @param productName   the name of the product that should be displayed.
         * @param productAmount the amount of products that should be displayed.
         * @param tags          the tags separated by comma.
         */
        public InputParamsHolder(View view, String productName, float productAmount, String tags){
            assignContextToEditViews(view);
            this.mView              = view;
        }

        /**
         * Checks if all editable fields are filled. Recommended to check before accessing product amount.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = false;
            returnValue |= getProductName().length() == 0;
            returnValue |= mProductAmountEditText.getText().length() == 0;
            returnValue |= mProductTagsEditText.getText().length() == 0;
            return !returnValue;
        }

        /**
         * call to show which elements aren't filled.
         */
        public void showUnFilledError(){

        }

        /**
         * checks if the input is correct.
         * @return true if  all is fine, false when some value is curious.
         */
        public boolean isValid(){
            return true;
        }

        /**
         * Show the elements that aren't valid.
         */
        public void showInvalid(){

        }

        /**
         * Gets the product name.
         * @return name of the product.
         */
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

        /**
         * Assigns the context to the edit view elements in this class. (like EditText)
         */
        private void assignContextToEditViews(View view){
            mProductNameEditText    = (EditText) view.findViewById(R.id.product_details_product_name_edittext);
            mProductAmountEditText  = (EditText) view.findViewById(R.id.product_details_amount_edittext);
            mProductTagsEditText    = (EditText) view.findViewById(R.id.product_details_tag_edittext);
        }
    }


    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(! mInputParams.isFilled()) {
                mInputParams.showUnFilledError();
                return;
            }
            if(!mInputParams.isValid()){
                mInputParams.showInvalid();
                return;
            }

            Product product = ControllerFactory.getProductController().createProduct(
                    mInputParams.getProductName(),
                    null,
                    mInputParams.getProductAmount(),
                    0.1f
            );

            if(product != null) {
                String[] tagArray = mInputParams.getTags();
                for (int Index = 0; Index < tagArray.length; ++Index) {
                    Tag tag = new Tag(tagArray[Index]);
                    ControllerFactory.getProductController().addTagToProduct(product, tag);
                }
            }

            if(product == null){
                Toast.makeText(getActivity(),"Addition of product failed!", Toast.LENGTH_LONG);
            }else{
                Toast.makeText(getActivity(),"Addition of product succeeded!", Toast.LENGTH_LONG);
            }

            // go to the last fragment where the creationwas initiated
            getFragmentManager().popBackStack();
        }
    };

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