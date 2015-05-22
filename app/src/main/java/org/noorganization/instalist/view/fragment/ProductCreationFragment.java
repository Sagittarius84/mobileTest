package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.List;

/**
 * Fragment where the creation and the editing of an product is handled.
 * Created by TS on 28.04.2015.
 */
public class ProductCreationFragment extends Fragment {

    public static final String ARGS_LIST_NAME = "listName";
    public static final String ARGS_PRODUCT_ID = "productId";
    private ShoppingList        mCurrentShoppingList;
    private InputParamsHolder   mInputParams;
    private Context             mContext;

    /**
     * used when product values should be rendered into view.
     */
    private Product             mProduct;

    private Button mAddButton;
    private Button mDecAmountButton;
    private Button mIncAmountButton;


    /**
     * Holds the input parameter views. Also delivers methods to retrieve the content of these
     * views.
     */
    private final static class InputParamsHolder{
        private EditText mProductNameEditText;
        private EditText mProductAmountEditText;
        private EditText mProductTagsEditText;
        private Context  mContext;

        public InputParamsHolder(View _View, Context _Context){
            assignContextToEditViews(_View);
            mProductAmountEditText.setText(String.valueOf(0.0f));
            this.mContext = _Context;
        }

        /**
         * Constructor of InputParamsHolder
         * @param _View         the view of the calling element.
         * @param _Context      the context of the fragment.
         * @param _Product      the reference to the product that should be rendered into the view.
         */
        public InputParamsHolder(View _View, Context _Context, Product _Product){
            assignContextToEditViews(_View);
            this.mContext = _Context;

            this.mProductAmountEditText.setText(String.valueOf(_Product.mDefaultAmount));
            this.mProductNameEditText.setText(String.valueOf(_Product.mName));

            List<TaggedProduct> taggedProductList = TaggedProduct.findTaggedProductsByProduct(_Product);
            if(taggedProductList.isEmpty()){
                return;
            }

            String tags = "";
            for(TaggedProduct taggedProduct : taggedProductList){
                tags = tags.concat(taggedProduct.mTag.mName).concat(",");
            }
            if(taggedProductList.size() > 1) {
                tags = tags.substring(0, tags.length() - 2);
            }
            this.mProductTagsEditText.setText(tags);
        }

        /**
         * Checks if all editable fields are filled. Recommended to check before accessing product amount.
         * Marks an unfilled entry as not filled.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = true;
            returnValue &= ViewUtils.checkTextViewIsFilled(mProductNameEditText);
            returnValue &= ViewUtils.checkTextViewIsFilled(mProductAmountEditText);
            // check if amount is greater than zero
            return returnValue;
        }

        /**
         * checks if the input matches the conventions.
         * @return true if  all is fine, false when some value is curious.
         */
        public boolean isValid(){
            boolean returnValue = true;

            float amount = Float.valueOf(mProductAmountEditText.getText().toString());
            if(amount <= 0.0f){
                Toast.makeText(mContext, mContext.getResources().getText(R.string.product_creation_fragment), Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

            return returnValue;
        }


        /**
         * Gets the product name.
         * @return name of the product.
         */
        public String getProductName(){
            return mProductNameEditText.getText().toString();
        }

        /**
         * Creates a float value of the amount input.
         * @return  a float value of the amount input. if edittext is set the value of this, else 0.0f.
         */
        public float getProductAmount(){
            if(mProductAmountEditText.getText().length() > 0) {
                return Float.valueOf(mProductAmountEditText.getText().toString());
            }
            return 0.0f;
        }

        /**
         * Splits the tags in the given edittext. Separator is comma.
         * @return a string array of extracted tags.
         */
        public String[] getTags(){
            if(mProductTagsEditText.getText().length() > 0) {
                    String tag = mProductTagsEditText.getText().toString();
                    return tag.split(",");
            }
            return new String[]{""};
        }

        /**
         * Increaese the amount of a product by 1.0f.
         */
        public void incProductAmount(){
            changeProductAmount(1.0f);
        }

        /**
         * decreaes the amount of a product by 1.0f.
         */
        public void decProductAmount(){
            changeProductAmount(-1.0f);
        }

        /**
         * Adds the given value to the productAmount. Takes care when value is less than 0.0f(resets it to 0.0f).
         * @param changeValue the value that should be added/substracted.
         */
        private void changeProductAmount(float changeValue){
            float amount = getProductAmount();
            amount += changeValue;
            if(amount < 0.0f){
                amount = 0.0f;
            }
            mProductAmountEditText.setText(String.valueOf(amount));
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


    private View.OnClickListener mOnCreateProductClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(!mInputParams.isFilled()){
                return;
            }

            if(!mInputParams.isValid()){
                return;
            }

            boolean success = false;
            // no information previously introduced
            if(mProduct == null){
                // new product to insert
                success = saveProduct();
            }else{
                // update old product
                success = updateProduct();
            }

            if(success){

                Fragment newFragment;
                if(mProduct == null){
                    Toast.makeText(getActivity(),"Addition of product succeeded!", Toast.LENGTH_LONG).show();
                    newFragment = ShoppingListOverviewFragment.newInstance(mCurrentShoppingList.mName);

                }else{
                    Toast.makeText(getActivity(),"Update of product succeeded!", Toast.LENGTH_LONG).show();
                    newFragment = ProductListDialogFragment.newInstance(mCurrentShoppingList.mName);
                }

                // go to before choosed fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.commit();

            }else {
                if(mProduct == null){
                    Toast.makeText(getActivity(), "Product update failed!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Addition of product failed!", Toast.LENGTH_LONG).show();
                }

            }

        }

        /**
         * saves the new product.
         * @return true by success false by fail.
         */
        private boolean saveProduct(){
            Product product = ControllerFactory.getProductController().createProduct(
                    mInputParams.getProductName(),
                    null,
                    mInputParams.getProductAmount(),
                    0.1f
            );

            if(product == null) {
                return false;
            }

            if(saveTags(product)) {
                // add entry to list overview
                ControllerFactory.getListController().addOrChangeItem(mCurrentShoppingList, product, product.mDefaultAmount);
                return true;
            }

            return false;
        }

        /**
         * Updates the product given to the fragment.
         * @return true if updating was successful else false, if an error happened.
         */
        private boolean updateProduct(){
            mProduct.mName          = mInputParams.getProductName();
            mProduct.mDefaultAmount = mInputParams.getProductAmount();

            Product product = ControllerFactory.getProductController().modifyProduct(mProduct);
            if(product == null){
                return false;
            }

            return saveTags(product);
        }

        /**
         * Saves all the given tags.
         * @param _Product the product where they should be associated.
         * @return true if all goes well, false if something is wrong.
         */
        private boolean saveTags(Product _Product){
            String[] tagArray = mInputParams.getTags();
            for (int Index = 0; Index < tagArray.length; ++Index) {
                Tag tag = ControllerFactory.getTagController().createTag(tagArray[Index]);
                if(tag == null){
                    tag = Tag.find(Tag.class,"m_name = ?", tagArray[Index]).get(0);
                }
                if(!ControllerFactory.getProductController().addTagToProduct(_Product, tag)){
                    return false;
                }
            }
            return true;
        }
    };



    /**
     * Creates an instance of an ProductCreationFragment with the details of the product.
     * @param _ListName the name of the list where the product should be added.
     * @return the new instance of this fragment.
     */
    public static ProductCreationFragment newInstance(String _ListName){
        ProductCreationFragment fragment = new ProductCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_NAME, _ListName);
        args.putLong(ARGS_PRODUCT_ID, -1L);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates an instance of an ProductCreationFragment.
     * @param _ProductId the id in the database of the product that should be edited.
     * @param _ListName the name of the list where the calling productlistselector should save the products.
     * @return the new instance of this fragment.
     */
    public static ProductCreationFragment newInstance(String _ListName, long _ProductId){
        ProductCreationFragment fragment = new ProductCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_NAME, _ListName);
        args.putLong(ARGS_PRODUCT_ID, _ProductId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.LIST_NAME_ATTR + "=?", getArguments().getString("listName")).get(0);

        // check if an product should be shown
        if(getArguments().getInt(ARGS_PRODUCT_ID) >= 0){
            long productId = getArguments().getLong(ARGS_PRODUCT_ID);
            mProduct = Product.findById(Product.class, productId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        if(mProduct == null) {
            mInputParams = new InputParamsHolder(view, getActivity());
        } else{
            mInputParams = new InputParamsHolder(view, getActivity(), mProduct);
        }
        mAddButton = (Button) view.findViewById(R.id.product_details_action_button_new_or_update);
        mDecAmountButton = (Button) view.findViewById(R.id.product_details_dec_amount);
        mIncAmountButton = (Button) view.findViewById(R.id.product_details_inc_amount);
        mIncAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputParams.incProductAmount();
            }
        });

        mDecAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputParams.decProductAmount();
            }
        });
        mAddButton.setOnClickListener(mOnCreateProductClickListener);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDecAmountButton.setOnClickListener(null);
        mIncAmountButton.setOnClickListener(null);
        mIncAmountButton.setOnClickListener(null);
    }
}