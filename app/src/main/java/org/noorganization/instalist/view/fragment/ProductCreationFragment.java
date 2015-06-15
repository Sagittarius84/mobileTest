package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.orm.query.Select;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Tag;
import org.noorganization.instalist.model.TaggedProduct;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.view.customview.AmountPicker;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Fragment where the creation and the editing of an product is handled.
 * Created by TS on 28.04.2015.
 */
public class ProductCreationFragment extends DialogFragment {

    public static final String ARGS_LIST_NAME = "listName";
    public static final String ARGS_PRODUCT_ID = "productId";
    private ShoppingList        mCurrentShoppingList;
    private InputParamsHolder   mInputParams;
    private Context             mContext;
    private IBaseActivity       mBaseActivityInterface;
    /**
     * used when product values should be rendered into view.
     */
    private Product     mProduct;
    private Button      mAddButton;


    /**
     * Holds the input parameter views. Also delivers methods to retrieve the content of these
     * views.
     */
    private final static class InputParamsHolder{
        private EditText     mProductName;
        private AmountPicker mProductAmount;
        private EditText     mProductTags;
        private EditText     mProductStep;
        private CheckBox     mProductAdvancedSwitch;
        private LinearLayout mProductAdvancedContents;
        private Spinner      mUnits;
        private Context      mContext;
        private List<Unit>   mUnitList;

        public InputParamsHolder(View _View, Context _Context){
            this.mContext = _Context;
            initViews(_View);

            mProductAmount.setValue(1.0f);
        }

        /**
         * Constructor of InputParamsHolder
         * @param _View         the view of the calling element.
         * @param _Context      the context of the fragment.
         * @param _Product      the reference to the product that should be rendered into the view.
         */
        public InputParamsHolder(View _View, Context _Context, Product _Product) {
            this.mContext = _Context;
            initViews(_View);

            this.mProductAmount.setValue(_Product.mDefaultAmount);
            this.mProductName.setText(_Product.mName);

            List<TaggedProduct> taggedProductList = TaggedProduct.findTaggedProductsByProduct(_Product);
            if(taggedProductList.isEmpty()){
                return;
            }

            String tags = "";
            for(TaggedProduct taggedProduct : taggedProductList){
                tags = tags.concat(taggedProduct.mTag.mName).concat(", ");
            }
            if(taggedProductList.size() > 1) {
                tags = tags.substring(0, tags.length() - 2);
            }
            this.mProductTags.setText(tags);
        }

        /**
         * Checks if all editable fields are filled. Recommended to check before accessing product amount.
         * Marks an unfilled entry as not filled.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = true;
            returnValue &= ViewUtils.checkTextViewIsFilled(mProductName);
            returnValue &= mProductAmount.getValue() > 0.0f;
            // check if amount is greater than zero
            return returnValue;
        }

        /**         * checks if the input matches the conventions.
         * @return true if  all is fine, false when some value is curious.
         */
        public boolean isValid(){
            boolean returnValue = true;

            float amount = mProductAmount.getValue();
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
            return mProductName.getText().toString();
        }

        /**
         * Creates a float value of the amount input.
         * @return  a float value of the amount input. if edittext is set the value of this, else 0.0f.
         */
        public float getProductAmount(){
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
         * @return a string array of extracted tags.
         */
        public String[] getTags(){
            String tagValue = mProductTags.getText().toString();
            LinkedList<String> rtn = new LinkedList<>(Arrays.asList(tagValue.split("\\s*,\\s*")));
            int last_size = rtn.size() + 1;
            while (last_size > rtn.size()) {
                last_size = rtn.size();
                rtn.remove("");
            }
            return rtn.toArray(new String[rtn.size()]);
        }

        /**
         * Adds the given value to the productAmount. Takes care when value is less than 0.0f(resets it to 0.0f).
         * @param _changeValue the value that should be added/substracted.
         */
        private void changeProductAmount(float _changeValue){
            float amount = getProductAmount();
            amount += _changeValue;
            if(amount < 0.0f){
                amount = 0.0f;
            }
            mProductAmount.setValue(amount);
        }
        /**
         * Assigns the context to the edit view elements in this class. (like EditText)
         */
        private void initViews(View _parentView){
            mProductName             = (EditText) _parentView.findViewById(R.id.product_details_product_name);
            mProductAmount           = (AmountPicker) _parentView.findViewById(R.id.product_details_amount);
            mProductTags             = (EditText) _parentView.findViewById(R.id.product_details_tag);
            mProductStep             = (EditText) _parentView.findViewById(R.id.product_details_step);
            mProductAdvancedSwitch   = (CheckBox) _parentView.findViewById(R.id.product_details_advanced);
            mProductAdvancedContents = (LinearLayout) _parentView.
                    findViewById(R.id.product_details_advanced_contents);
            mUnits                   = (Spinner) _parentView.findViewById(R.id.product_details_unit);

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

            mUnitList = Select.from(Unit.class).orderBy(Unit.ATTR_NAME).list();
            String[] displayUnitStrings = new String[mUnitList.size()];
            for (int currentUnitIndex = 0; currentUnitIndex < mUnitList.size(); currentUnitIndex++) {
                displayUnitStrings[currentUnitIndex] = mUnitList.get(currentUnitIndex).mName;
            }
            mUnits.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line,
                    displayUnitStrings));
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

                //Fragment newFragment;
                if(mProduct == null){
                    Toast.makeText(getActivity(),"Addition of product succeeded!", Toast.LENGTH_LONG).show();
                    //   newFragment = ShoppingListOverviewFragment.newInstance(mCurrentShoppingList.mName);

                }else{
                    Toast.makeText(getActivity(),"Update of product succeeded!", Toast.LENGTH_LONG).show();
                //    newFragment = ProductListDialogFragment.newInstance(mCurrentShoppingList.mName);
                }

                //changeFragment(newFragment);


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
                    mInputParams.getProductStep()
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
            mProduct.mStepAmount    = mInputParams.getProductStep();

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
    public void onAttach(Activity _Activity) {
        super.onAttach(_Activity);
        mContext = _Activity;
        try {
            mBaseActivityInterface = (IBaseActivity) _Activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Activity.toString()
                    + " has no IBaseActivity interface attached.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.ATTR_NAME + "=?", getArguments().getString("listName")).get(0);

        // check if an product should be shown
        if(getArguments().getLong(ARGS_PRODUCT_ID) >= 0){
            long productId = getArguments().getLong(ARGS_PRODUCT_ID);
            mProduct = Product.findById(Product.class, productId);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);
        if(mProduct == null) {
            mInputParams = new InputParamsHolder(view, getActivity());
        } else{
            mInputParams = new InputParamsHolder(view, getActivity(), mProduct);
        }
        mAddButton = (Button) view.findViewById(R.id.product_details_action_button_new_or_update);
        mAddButton.setOnClickListener(mOnCreateProductClickListener);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}