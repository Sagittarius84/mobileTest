package org.noorganization.instalist.presenter.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.presenter.dataholder.RecipeDataHolder;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.spinneradapter.ProductSpinnerAdapter;
import org.noorganization.instalist.presenter.utils.ViewUtils;

import java.util.List;

/**
 * The IngredientCreationFragment handles the creation and editing of an ingredient.
 * Created by TS on 24.05.2015.
 */
public class IngredientCreationFragment extends BaseFragment {

    private static final String ARGS_INGREDIENT_ID = "ingredient_id";
    private static final String ARGS_INGREDIENT_LIST_INDEX = "ingredient_list_index";

    /**
     * The instance of add ingredient button.
     */
    private Button mAddIngredientButton;

    /**
     * The instance of cancel button.
     */
    private Button mCancelButton;

    /**
     * The list of products that is shown within the spinner.
     */
    private List<Product> mListOfProducts;

    /**
     * The ingredient that should be edited when not null, else it is a new ingredient.
     * Used as flag to indicate if a ingredient is new(null) or not(ingredient is set).
     */
    private Ingredient mIngredient = null;

    /**
     * The reference to the ViewAcccessor.
     *
     * @see org.noorganization.instalist.presenter.fragment.IngredientCreationFragment.ViewAcccessor
     */
    private ViewAcccessor mViewAccessor;

    private IBaseActivity mBaseActivityInterface;
    private Context mContext;

    /**
     * Creates a IngredientCreationFragment with the information of an ingredient filled, that database id was given as parameter.
     *
     * @param _IngredientId the id of the database entry of this ingredient.
     * @return an instance of the fragment with set values of the given data.
     */
    public static IngredientCreationFragment newInstance(long _IngredientId) {
        IngredientCreationFragment fragment = new IngredientCreationFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_INGREDIENT_ID, _IngredientId);
        args.putInt(ARGS_INGREDIENT_LIST_INDEX, -1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a IngredientCreationFragment with the information of an ingredient filled, that index id was given as parameter.
     *
     * @param _IngredientListIndex the list index for the ingredientlist hold in RecipeDataHolder.
     * @return an instance of the fragment with set values of the given data.
     */
    public static IngredientCreationFragment newInstance(int _IngredientListIndex) {
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
     *
     * @return an instance of the fragment with set values of the given data.
     */
    public static IngredientCreationFragment newInstance() {
        IngredientCreationFragment fragment = new IngredientCreationFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_INGREDIENT_ID, -1L);
        args.putInt(ARGS_INGREDIENT_LIST_INDEX, -1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onAttachToContext(Context _Context) {
        mContext = _Context;
        try {
            mBaseActivityInterface = (IBaseActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " has no IBaseActivity interface attached.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListOfProducts = ControllerFactory.getProductController(mContext).listAll();
        List<Ingredient> ingredients = RecipeDataHolder.getInstance().getIngredients();

        if (ingredients == null) {
            throw new NullPointerException("The list of ingredients in RecipeDataHolder is not set.");
        }

        for (Ingredient ingredient : ingredients) {
            mListOfProducts.remove(ingredient.mProduct);
        }

        // check if an product should be shown
        if (getArguments().getString(ARGS_INGREDIENT_ID) != null) {
            String ingredientId = getArguments().getString(ARGS_INGREDIENT_ID);
            mIngredient = ControllerFactory.getRecipeController(mContext).findIngredientById(ingredientId);
        } else {
            // try to get the Ingredient by its index
            int listIndex = getArguments().getInt(ARGS_INGREDIENT_LIST_INDEX);
            if (listIndex >= 0) {
                if (listIndex > ingredients.size()) {
                    throw new IndexOutOfBoundsException("The given listIndex is bigger than the ingredients size.");
                }
                mIngredient = ingredients.get(listIndex);
            }
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        super.onCreateView(_Inflater, _Container, _SavedInstanceState);

        String titleString;
        View view = _Inflater.inflate(R.layout.fragment_ingredient_creation, _Container, false);

        mAddIngredientButton = (Button) view.findViewById(R.id.fragment_ingredient_creation_button_add_ingredient);
        mCancelButton = (Button) view.findViewById(R.id.fragment_ingredient_creation_button_cancel);

        if (mIngredient == null) {
            mViewAccessor = new ViewAcccessor(view, mContext, mListOfProducts);
            mAddIngredientButton.setText(mContext.getText(R.string.fragment_ingredient_creation_add_ingredient));
            titleString = mContext.getText(R.string.fragment_ingredient_creation_add_ingredient_title).toString();
        } else {
            mViewAccessor = new ViewAcccessor(view, mContext, mListOfProducts, mIngredient);
            mAddIngredientButton.setText(mContext.getText(R.string.fragment_ingredient_creation_update_ingredient));
            titleString = mContext.getText(R.string.fragment_ingredient_creation_update_ingredient_title).toString();

        }

        titleString = titleString.concat(" " + RecipeDataHolder.getInstance().getRecipeName());
        mBaseActivityInterface.setToolbarTitle(titleString);
        return view;
    }

    /**
     * Add all clicklisteners.
     */
    @Override
    public void onResume() {
        super.onResume();

        // add or update an ingredient
        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if data is filled in.
                if (!mViewAccessor.isFilled()) {
                    return;
                }

                if (!mViewAccessor.isValid()) {
                    return;
                }

                // get the ingredients that are currently assigned to the recipe.
                List<Ingredient> ingredients = RecipeDataHolder.getInstance().getIngredients();

                if (mIngredient != null) {
                    update(ingredients);
                } else {
                    save(ingredients);
                }

                // push the changed data to the currently edited recipe.
                RecipeDataHolder.getInstance().setIngredients(ingredients);
                // TODO: return to the recipe
                //mBaseActivityInterface.changeFragment(RecipeChangeActivity.newInstance(""));
            }

            /**
             * Adds the user generated ingredient to the list of ingredients of the current recipe.
             * @param _Ingredients the list of ingredients of the current recipe. Commonly from RecipeDataHolder.
             */
            public void save(List<Ingredient> _Ingredients) {
                Ingredient ingredient = new Ingredient();
                ingredient.mAmount = mViewAccessor.getIngredientAmount();
                ingredient.mProduct = mViewAccessor.getSelectedProduct();
                _Ingredients.add(ingredient);
            }

            /**
             * Overrides an existing Ingredient in the list of ingredients of the current recipe.
             * @param _Ingredients the list of ingredients of the current recipe. Commonly from RecipeDataHolder.
             */
            public void update(List<Ingredient> _Ingredients) {
                int index = _Ingredients.indexOf(mIngredient);
                Ingredient ingredient = _Ingredients.get(index);
                ingredient.mAmount = mViewAccessor.getIngredientAmount();
                ingredient.mProduct = mViewAccessor.getSelectedProduct();
                _Ingredients.set(index, ingredient);
            }
        });

        // only go back to calling fragment, wenn cancel was pressed.
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivityInterface.onBackPressed();
            }
        });
    }

    /**
     * Remove all clicklisteners.
     */
    @Override
    public void onPause() {
        super.onPause();
        mAddIngredientButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
    }

    /**
     * The ViewAcccessor class provides methods to set the data to the view
     * and to get data from the view.
     */
    private class ViewAcccessor {

        /**
         * The EditText reference to the amount of a product for a ingredient.
         */
        private EditText mIngredientAmountEditText;

        /**
         * The Spinner of the chooseable product to add as ingredient.
         */
        private Spinner mProductSpinner;

        /**
         * The view that is currently shown.
         */
        private View mView;

        /**
         * The context of the application.
         */
        private Context mContext;

        /**
         * The ProductSpinnerAdapter that holds the elements for mProductSpinner.
         * In short it holds all selectable products.
         */
        private ProductSpinnerAdapter mProductSpinnerAdapter;

        /**
         * The Ingredient reference that is currently edited,
         * null if there is no Ingredient to be edited.
         */
        private Ingredient mIngredient;

        /**
         * Initializes all views and sets the view reference.
         *
         * @param _View        the view that is currently used.
         * @param _Context     the context of the activity.
         * @param _ProductList the list of products that can be choosen, without the already existent products.
         */
        public ViewAcccessor(View _View, Context _Context, List<Product> _ProductList) {
            mView = _View;
            mContext = _Context;
            assignIds();

            mIngredientAmountEditText.setText(String.valueOf(0.0f));
            mProductSpinnerAdapter = new ProductSpinnerAdapter((Activity) _Context, _ProductList);
            mProductSpinner.setAdapter(mProductSpinnerAdapter);
        }

        /**
         * Initializes all views and sets the view references. Also it sets all by the user editable fields.
         *
         * @param _View        the view that is currently used.
         * @param _Context     the context of the activity.
         * @param _ProductList the list of products that can be choosen, without the already existent products.
         * @param _Ingredient  the ingredient that should be filled in.
         */
        public ViewAcccessor(View _View, Context _Context, List<Product> _ProductList, Ingredient _Ingredient) {
            mView = _View;
            mContext = _Context;
            assignIds();

            mIngredient = _Ingredient;
            mIngredientAmountEditText.setText(String.valueOf(mIngredient.mAmount));

            // add the product that is currently in edit process to the list of chooseable products
            _ProductList.add(mIngredient.mProduct);
            mProductSpinnerAdapter = new ProductSpinnerAdapter((Activity) _Context, _ProductList);
            mProductSpinner.setAdapter(mProductSpinnerAdapter);
            // set the selection on the product that is currently in edit process
            mProductSpinner.setSelection(mProductSpinnerAdapter.getPosition(_Ingredient.mProduct));
        }


        /**
         * Checks if all editable fields are filled. Recommended to check before accessing ingredient amount.
         * Marks an unfilled entry as not filled.
         *
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled() {
            boolean returnValue;

            returnValue = ViewUtils.checkEditTextIsFilled(mIngredientAmountEditText);
            // check if at least on ingredient is there
            returnValue &= !mProductSpinnerAdapter.isEmpty();
            // show info that the user should assign a product at first
            if (mProductSpinnerAdapter.isEmpty()) {
                Toast.makeText(mContext, mContext.getResources().getText(R.string.fragment_ingredient_creation_no_product_assigned), Toast.LENGTH_SHORT).show();
            }
            return returnValue;
        }

        /**
         * Checks if the inserted data is valid and marks it.
         *
         * @return true if valid, false invalid.
         */
        public boolean isValid() {
            // check if value is out of range
            if (getIngredientAmount() <= 0.0f) {
                mIngredientAmountEditText.setError(mContext.getResources().getString(R.string.invalid_amount));
                return false;
            }

            return true;
        }

        /**
         * Assigns all related references to the single view components.
         */
        private void assignIds() {
            mIngredientAmountEditText = (EditText) mView.findViewById(R.id.fragment_ingredient_creation_edittext_amount);
            mProductSpinner = (Spinner) mView.findViewById(R.id.fragment_ingredient_creation_spinner_ingredient_product);
        }

        /**
         * Get the amount of the entered ingredient amount.
         *
         * @return the amount of the inserted text, else if there is no texxt then 0.0f.
         */
        public float getIngredientAmount() {
            if (mIngredientAmountEditText.getText().length() > 0) {
                return Float.valueOf(mIngredientAmountEditText.getText().toString());
            }
            return 0.0f;
        }

        /**
         * Get the selected product from the spinner.
         *
         * @return the choosen product, else if there is no product null.
         */
        public Product getSelectedProduct() {
            if (mProductSpinner.getCount() > 0) {
                return (Product) mProductSpinner.getSelectedItem();
            }
            return null;
        }


    }
}
