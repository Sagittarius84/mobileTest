package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.view.datahandler.RecipeDataHolder;
import org.noorganization.instalist.view.spinneradapter.ProductSpinnerAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.List;

/**
 * The IngredientCreationFragment handles the creation and editing of an ingredient.
 * Created by TS on 24.05.2015.
 */
public class IngredientCreationFragment extends BaseCustomFragment {

    private static final String ARGS_INGREDIENT_ID = "ingredient_id";
    private static final String ARGS_INGREDIENT_LIST_INDEX = "ingredient_list_index";

    private EditText mAmountEditText;
    private Spinner mIngredientSpinner;
    private Button mAddIngredientButton;
    private Button mCancelButton;

    private List<Product> mListOfProducts;
    private Ingredient mIngredient = null;
    private ViewAcccessor mViewAccessor;

    public static IngredientCreationFragment newInstance(long _IngredientId){
        IngredientCreationFragment fragment = new IngredientCreationFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_INGREDIENT_ID, _IngredientId);
        args.putInt(ARGS_INGREDIENT_LIST_INDEX, -1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a IngredientCreationFragment with the information of an ingredient filled, that index id was given as parameter.
     * @param _IngredientListIndex the list index for the ingredientlist hold in RecipeDataHolder.
     * @return the new instance of this fragment.
     */
    public static IngredientCreationFragment newInstance(int _IngredientListIndex){
        IngredientCreationFragment fragment = new IngredientCreationFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_INGREDIENT_ID, -1L);
        args.putInt(ARGS_INGREDIENT_LIST_INDEX, _IngredientListIndex);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Creates an instance of IngredientCreationFragment that enables the creation of a
     * new ingredient.
     * @return an instance of IngredientCreationFragment.
     */
    public static IngredientCreationFragment newInstance(){
        IngredientCreationFragment fragment = new IngredientCreationFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_INGREDIENT_ID, -1L);
        args.putInt(ARGS_INGREDIENT_LIST_INDEX, -1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListOfProducts = Product.listAll(Product.class);
        List<Ingredient> ingredients = RecipeDataHolder.getInstance().getIngredients();

        if(ingredients == null){
            throw new NullPointerException("The list of ingredients in RecipeDataHolder is not set.");
        }

        for(Ingredient ingredient : ingredients){
            mListOfProducts.remove(ingredient.mProduct);
        }

        // check if an product should be shown
        if(getArguments().getLong(ARGS_INGREDIENT_ID) >= 0){
            long ingredientId = getArguments().getLong(ARGS_INGREDIENT_ID);
            mIngredient = Ingredient.findById(Ingredient.class, ingredientId);
        }else{
            int listIndex = getArguments().getInt(ARGS_INGREDIENT_LIST_INDEX);
            if( listIndex >= 0){
                if(listIndex > ingredients.size()){
                    throw new IndexOutOfBoundsException("The given listIndex is bigger than the ingredients size.");
                }
                mIngredient = ingredients.get(listIndex);
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_ingredient_creation, container, false);
        mAmountEditText = (EditText) view.findViewById(R.id.fragment_ingredient_creation_edittext_amount);
        mIngredientSpinner = (Spinner) view.findViewById(R.id.fragment_ingredient_creation_spinner_ingredient_product);
        mAddIngredientButton = (Button) view.findViewById(R.id.fragment_ingredient_creation_button_add_ingredient);
        mCancelButton = (Button) view.findViewById(R.id.fragment_ingredient_creation_button_cancel);

        //mIngredientSpinner.setAdapter(new ProductSpinnerAdapter(mActivity, mListOfProducts));


        if(mIngredient == null) {
            mViewAccessor = new ViewAcccessor(view, mActivity, mListOfProducts);
        }else{
            mViewAccessor = new ViewAcccessor(view, mActivity, mListOfProducts, mIngredient);
        }

        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mViewAccessor.isFilled()){
                    return;
                }

                List<Ingredient> ingredients = RecipeDataHolder.getInstance().getIngredients();

                if(mIngredient != null){
                    int index = ingredients.indexOf(mIngredient);
                    Ingredient ingredient = ingredients.get(index);
                    ingredient.mAmount = mViewAccessor.getIngredientAmount();
                    ingredient.mProduct = mViewAccessor.getSelectedProduct();
                    ingredients.set(index, ingredient);
                }else{
                    Ingredient ingredient = new Ingredient();
                    ingredient.mAmount = mViewAccessor.getIngredientAmount();
                    ingredient.mProduct = mViewAccessor.getSelectedProduct();
                    ingredients.add(ingredient);
                }

                RecipeDataHolder.getInstance().setIngredients(ingredients);
                changeFragment(RecipeCreationFragment.getInstance());
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return view;
    }


    private class ViewAcccessor
    {

        private EditText mIngredientAmountEditText;
        private Spinner  mProductSpinner;

        private View        mView;
        private Context     mContext;

        private ProductSpinnerAdapter mProductSpinnerAdapter;
        private Ingredient mIngredient;

        /**
         * Initializes all views and sets the view reference.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         * @param _ProductList the list of products that can be choosen, without the already existent products.
         */
        public ViewAcccessor(View _View, Context _Context, List<Product> _ProductList){
            this.mView = _View;
            this.mContext = _Context;
            assignIds();

            mIngredientAmountEditText.setText(String.valueOf(0.0f));
            this.mProductSpinnerAdapter = new ProductSpinnerAdapter((Activity) _Context, _ProductList);
            mProductSpinner.setAdapter(this.mProductSpinnerAdapter);
        }

        /**
         * Initializes all views and sets the view references. Also it sets all by the user editable fields.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         * @param _ProductList the list of products that can be choosen, without the already existent products.
         * @param _Ingredient the ingredient that should be filled in.
         */
        public ViewAcccessor(View _View, Context _Context, List<Product> _ProductList, Ingredient _Ingredient){

            this.mView = _View;
            this.mContext = _Context;
            assignIds();

            _ProductList.add(_Ingredient.mProduct);

            this.mIngredient = _Ingredient;
            this.mIngredientAmountEditText.setText(String.valueOf(_Ingredient.mAmount));
            this.mProductSpinnerAdapter = new ProductSpinnerAdapter((Activity) _Context, _ProductList);
            this.mProductSpinner.setAdapter(this.mProductSpinnerAdapter);
            this.mProductSpinner.setSelection(mProductSpinnerAdapter.getPosition(_Ingredient.mProduct));
        }


        /**
         * Checks if all editable fields are filled. Recommended to check before accessing ingredient amount.
         * Marks an unfilled entry as not filled.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = true;
            returnValue &= ViewUtils.checkTextViewIsFilled(mIngredientAmountEditText);
            // check if at least on ingredient is there
            returnValue &= !mProductSpinnerAdapter.isEmpty();
            return returnValue;
        }

        /**
         * Assigns all related references to the single view components.
         */
        private void assignIds(){
            mIngredientAmountEditText = (EditText) mView.findViewById(R.id.fragment_ingredient_creation_edittext_amount);
            mProductSpinner = (Spinner) mView.findViewById(R.id.fragment_ingredient_creation_spinner_ingredient_product);
        }

        /**
         * Get the amount of the entered ingredient amount.
         * @return the amount of the inserted text, else if there is no texxt then 0.0f.
         */
        public float getIngredientAmount(){
            if(mIngredientAmountEditText.getText().length() > 0) {
                return Float.valueOf(mIngredientAmountEditText.getText().toString());
            }
            return 0.0f;
        }

        /**
         * Get the selected product from the spinner.
         * @return the choosen product, else if there is no product null.
         */
        public Product getSelectedProduct(){
            if(mProductSpinner.getCount() > 0) {
                return (Product) mProductSpinner.getSelectedItem();
            }
            return null;
        }


    }
}
