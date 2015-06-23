package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.datahandler.RecipeDataHolder;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.listadapter.IngredientListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that handles the creation of a recipe.
 * Created by TS on 28.04.2015.
 */
public class RecipeCreationFragment extends Fragment {

    private static final String ARGS_LIST_NAME = "list_name";
    private static final String ARGS_RECIPE_ID = "recipe_id";


    private ShoppingList mCurrentShoppingList;
    private ViewAcccessor mViewAccessor;
    private Recipe  mRecipe;
    private Button mAddIngredientButton;
    private Button mAddRecipeButton;
    private Button mCancelButton;

    private IBaseActivity mBaseActivityInterface;
    private Context mContext;

    private static RecipeCreationFragment mInstance;

    private View.OnClickListener mOnAddRecipeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(!mViewAccessor.isFilled()){
                return;
            }
            if(!mViewAccessor.isValid()){
                return;
            }

            boolean success;

            if(mRecipe == null){
                success = saveRecipe();
            }else
            {
                success = updateRecipe();
            }

            // if saving was no success so show some error message and do nothing more!
            if(!success){
                if(mRecipe == null){
                    Toast.makeText(getActivity(), "Recipe update failed!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Addition of recipe failed!", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // success case begins
            Fragment fragment;
            if(mRecipe == null){
                fragment = ShoppingListOverviewFragment.newInstance(mCurrentShoppingList.mName);
            }else{
                fragment = ProductListDialogFragment.newInstance(mCurrentShoppingList.getId());
            }
            // clear the data holder so that we not retrieve later the currently inserted data.
            RecipeDataHolder.getInstance().clear();
            mBaseActivityInterface.changeFragment(fragment);
        }

        private boolean updateRecipe(){
            String recipeName = mViewAccessor.getRecipeName();
            List<Ingredient> ingredientList = mViewAccessor.getRecipeIngredients();
            String[] tagArray               = mViewAccessor.getRecipeTags();
            if(tagArray != null){
                // save the tags!
            }
            Recipe recipe = mRecipe;
            if(recipe == null){
                return false;
            }

            List<Ingredient> existingIngredients = recipe.getIngredients();
            List<Ingredient> removedIngredients = mViewAccessor.getRemovedIngredients();

            for(Ingredient ingredient : ingredientList){
                Ingredient ingredientCreated = ControllerFactory.getRecipeController().addOrChangeIngredient(recipe, ingredient.mProduct, ingredient.mAmount);
                if(ingredientCreated == null){
                    // amount is set wrong
                    return false;
                }
            }


            // remove all removed ingredients
            for(Ingredient ingredient : removedIngredients){
                ControllerFactory.getRecipeController().removeIngredient(ingredient);
            }

            // change the name of recipe
            if(!mRecipe.mName.equals(recipeName)) {
                Recipe recipe1 = ControllerFactory.getRecipeController().renameRecipe(recipe, recipeName);
                if(!recipe1.mName.equals(recipeName)){
                    return false;
                }
            }

            return true;
        }

        private boolean saveRecipe(){
            String recipeName               = mViewAccessor.getRecipeName();
            List<Ingredient> ingredientList = mViewAccessor.getRecipeIngredients();
            String[] tagArray               = mViewAccessor.getRecipeTags();

            if(tagArray != null){
                // save the tags!
            }
            Recipe recipe = ControllerFactory.getRecipeController().createRecipe(recipeName);
            if(recipe == null){
                return false;
            }

            for(Ingredient ingredient : ingredientList){
                Ingredient ingredientCreated = ControllerFactory.getRecipeController().addOrChangeIngredient(recipe, ingredient.mProduct, ingredient.mAmount);
                if(ingredientCreated == null){
                    // amount is set wrong
                    return false;
                }
            }

            return true;
        }
    };

    /**
     * Creates an instance of an ProductChangeFragment with the details of the product.
     * @param _ListName the name of the list where the product should be added.
     * @return the new instance of this fragment.
     */
    public static RecipeCreationFragment newInstance(String _ListName){
        RecipeCreationFragment fragment = new RecipeCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_NAME, _ListName);
        args.putLong(ARGS_RECIPE_ID, -1L);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }


    /**
     * Creates an instance of an ProductChangeFragment.
     * @param _RecipeId the id in the database of the product that should be edited.
     * @param _ListName the name of the list where the calling productlistselector should save the products.
     * @return the new instance of this fragment.
     */
    public static RecipeCreationFragment newInstance(String _ListName, long _RecipeId){
        RecipeCreationFragment fragment = new RecipeCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_NAME, _ListName);
        args.putLong(ARGS_RECIPE_ID, _RecipeId);
        fragment.setArguments(args);
        mInstance = fragment;
        return fragment;
    }

    public static RecipeCreationFragment getInstance(){
        return mInstance;
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
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.ATTR_NAME + "=?", getArguments().getString(ARGS_LIST_NAME)).get(0);

        // check if an product should be shown
        if(getArguments().getInt(ARGS_RECIPE_ID) >= 0){
            long recipeId = getArguments().getLong(ARGS_RECIPE_ID);
            mRecipe = Recipe.findById(Recipe.class, recipeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        super.onCreateView(_Inflater, _Container, _SavedInstanceState);

        String titleString;
        View view = _Inflater.inflate(R.layout.fragment_recipe_details, _Container, false);

        mAddIngredientButton = (Button) view.findViewById(R.id.fragment_recipe_details_add_ingredient);
        mAddRecipeButton     = (Button) view.findViewById(R.id.fragment_recipe_details_add_recipe);
        mCancelButton        = (Button) view.findViewById(R.id.fragment_recipe_details_cancel_recipe);

        if(mRecipe == null) {
            mViewAccessor = new ViewAcccessor(view, getActivity());
            mAddRecipeButton.setText(mContext.getResources().getString(R.string.fragment_recipe_creation_add_recipe));
            titleString = mContext.getResources().getString(R.string.fragment_recipe_creation_add_recipe_title);
        } else{
            mViewAccessor = new ViewAcccessor(view, getActivity(), mRecipe);
            mAddRecipeButton.setText(mContext.getResources().getString(R.string.fragment_recipe_creation_update_recipe));
            titleString = mContext.getResources().getString(R.string.fragment_recipe_creation_update_recipe_title);
            titleString = titleString.concat(" " + mRecipe.mName);
        }

        if(RecipeDataHolder.getInstance().hasSetValues()){
            mViewAccessor.loadRecipeFromDataHolder();
        }

        mBaseActivityInterface.setToolbarTitle(titleString);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAddIngredientButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // mViewAccessor.addIngredient();
                mViewAccessor.saveRecipeToDataHolder();
                mBaseActivityInterface.changeFragment(IngredientCreationFragment.newInstance());
            }
        });

        mAddRecipeButton.setOnClickListener(mOnAddRecipeClickListener);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivityInterface.onBackPressed();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mAddIngredientButton.setOnClickListener(null);
        mAddRecipeButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
    }

    private final static class ViewAcccessor{

        private EditText    mRecipeNameText;
        private ListView    mIngredientListView;
        private EditText    mRecipeTagText;
        private View        mView;
        private Context     mContext;

        private IngredientListAdapter mIngredientListAdapter;
        private Recipe      mRecipe;
        private RecipeDataHolder mRecipeDataHolder;

        /**
         * Initializes all views and sets the view reference.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         */
        public ViewAcccessor(View _View, Context _Context){
            this.mView = _View;
            this.mContext = _Context;
            assignIds();

            mRecipeDataHolder = RecipeDataHolder.getInstance();
            List<Ingredient> ingredientList = mRecipeDataHolder.getIngredients();

            if(ingredientList.size() > 0){
                mIngredientListAdapter = new IngredientListAdapter((Activity) _Context, ingredientList);
            }
            else{
                mIngredientListAdapter = new IngredientListAdapter((Activity) _Context, new ArrayList<Ingredient>());
            }
            this.mRecipe = null;
            mIngredientListView.setAdapter(mIngredientListAdapter);
            mRecipeDataHolder.setIngredients(mIngredientListAdapter.getIngredients());
        }

        /**
         * Initializes all views and sets the view references. Also it sets all by the user editable fields.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         * @param _Recipe the recipe that should be filled in.
         */
        public ViewAcccessor(View _View, Context _Context, Recipe _Recipe){

           mView = _View;
           mContext = _Context;
           mRecipe = _Recipe;

            assignIds();
            mRecipeDataHolder = RecipeDataHolder.getInstance();
            mRecipeNameText.setText(_Recipe.mName);
            // mRecipeTagText.setText();

            mRecipeDataHolder = RecipeDataHolder.getInstance();
            if(mRecipeDataHolder.getIngredients() != null && mRecipeDataHolder.getIngredients().size() > 0){
                mIngredientListAdapter = new IngredientListAdapter((Activity) _Context, mRecipeDataHolder.getIngredients());
            }
            else{
                mIngredientListAdapter = new IngredientListAdapter((Activity) _Context, _Recipe.getIngredients());
            }
            mIngredientListView.setAdapter(mIngredientListAdapter);
            mRecipeDataHolder.setIngredients(mIngredientListAdapter.getIngredients());
        }

        /**
         * Checks if all editable fields are filled. Recommended to check before accessing product amount.
         * Marks an unfilled entry as not filled.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = ViewUtils.checkEditTextIsFilled(mRecipeNameText);
            // check if at least on ingredient is there
            returnValue &= mIngredientListAdapter.getCount() > 0;
            return returnValue;
        }

        /**
         * Assigns all related references to the single view components.
         */
        private void assignIds(){
            mRecipeNameText = (EditText) mView.findViewById(R.id.fragment_recipe_details_recipe_name);
            mRecipeTagText  = (EditText) mView.findViewById(R.id.fragment_recipe_details_recipe_tags);
            mIngredientListView = (ListView) mView.findViewById(R.id.fragment_recipe_details_ingredient_container);
        }

        public String getRecipeName(){
            return mRecipeNameText.getText().toString();
        }

        /**
         * Get the assigned tags from the recipe tags field
         * @return if any tags are entered an array of tags in string form, else you will receive null.
         */
        public String[] getRecipeTags(){
            if(mRecipeTagText.getText().length() > 0) {
                return mRecipeTagText.getText().toString().split(",");
            }
            return null;
        }

        public List<Ingredient> getRecipeIngredients(){
            return mIngredientListAdapter.getIngredients();
        }

        public List<Ingredient> getRemovedIngredients(){
            return mIngredientListAdapter.getRemovedIngredients();
        }
        /**
         * TODO: Add validation feature
         * @return true if input is valid, false if at least one input is invalid.
         */
        public boolean isValid() {
            return true;
        }


        /**
         * Saves the current recipe to the RecipeDataHolder.
         */
        public void saveRecipeToDataHolder(){
            long id = -1L;
            boolean isNew = true;
            String recipeName = "";
            if(this.mRecipe != null){
                id = this.mRecipe.getId();
                isNew = false;
            }

            if(this.mRecipeNameText.getText().length() > 0){
                recipeName = mRecipeNameText.getText().toString();
            }

            List<Ingredient> ingredients = getRecipeIngredients();
            List<Ingredient> removedIngredients = mIngredientListAdapter.getRemovedIngredients();

            mRecipeDataHolder.setValues(id, isNew, recipeName, ingredients, removedIngredients);
        }

        /**
         * Loads the recipe from the RecipeDataHolder.
         */
        public void loadRecipeFromDataHolder(){
            if(mRecipeDataHolder.isNew()){
                this.mRecipe = Recipe.findById(Recipe.class, mRecipeDataHolder.getRecipeID());
            }
            this.mRecipeNameText.setText(mRecipeDataHolder.getRecipeName());
            this.mIngredientListAdapter.setData(mRecipeDataHolder.getIngredients(), mRecipeDataHolder.getRemovedIngredients());
        }

    }
}

