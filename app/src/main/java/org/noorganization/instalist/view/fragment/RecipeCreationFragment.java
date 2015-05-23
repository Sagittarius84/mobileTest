package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.MainShoppingListView;
import org.noorganization.instalist.view.listadapter.IngredientListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by TS on 28.04.2015.
 */
public class RecipeCreationFragment extends BaseCustomFragment {

    private static final String ARGS_LIST_NAME = "list_name";
    private static final String ARGS_RECIPE_ID = "recipe_id";


    private ShoppingList mCurrentShoppingList;
    private ViewAcccessor mViewAccessor;
    private Recipe  mRecipe;
    private Button mAddIngredientButton;
    private IngredientListAdapter mIngredientListAdapter;

    /**
     * Creates an instance of an ProductCreationFragment with the details of the product.
     * @param _ListName the name of the list where the product should be added.
     * @return the new instance of this fragment.
     */
    public static RecipeCreationFragment newInstance(String _ListName){
        RecipeCreationFragment fragment = new RecipeCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_LIST_NAME, _ListName);
        args.putLong(ARGS_RECIPE_ID, -1L);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Creates an instance of an ProductCreationFragment.
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
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.LIST_NAME_ATTR + "=?", getArguments().getString(ARGS_LIST_NAME)).get(0);

        // check if an product should be shown
        if(getArguments().getInt(ARGS_RECIPE_ID) >= 0){
            long recipeId = getArguments().getLong(ARGS_RECIPE_ID);
            mRecipe = Recipe.findById(Recipe.class, recipeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        mViewAccessor = new ViewAcccessor(view, getActivity(), null);

        mAddIngredientButton = (Button) view.findViewById(R.id.fragment_recipe_details_add_ingredient);
        mAddIngredientButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mViewAccessor.addIngredient();
            }
        });
    /*    if(mRecipe == null) {
            mViewAccessor = new ViewAcccessor(view, getActivity());
        } else{
            mViewAccessor = new ViewAcccessor(view, getActivity(), mRecipe);
        }
      */
        return view;
    }


    private final static class ViewAcccessor{

        private EditText    mRecipeNameText;
        private ListView    mIngredientListView;
        private EditText    mRecipeTagText;
        private View        mView;
        private Context     mContext;

        private IngredientListAdapter mIngredientListAdapter;

        /**
         * Initializes all views and sets the view reference.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         */
        public ViewAcccessor(View _View, Context _Context){
            this.mView = _View;
            this.mContext = _Context;
            assignIds();
        }

        /**
         * Initializes all views and sets the view references. Also it sets all by the user editable fields.
         * @param _View the view that is currently used.
         * @param _Context the context of the activity.
         * @param _Recipe the recipe that should be filled in.
         */
        public ViewAcccessor(View _View, Context _Context, Recipe _Recipe){

            this.mView = _View;
            this.mContext = _Context;
            assignIds();

            //mRecipeNameText.setText(_Recipe.mName);
            //mRecipeTagText.setText();
            mIngredientListAdapter = new IngredientListAdapter((Activity) _Context, new ArrayList<Ingredient>());

            mIngredientListView.setAdapter( mIngredientListAdapter);
        }

        public void addIngredient(){
            mIngredientListAdapter.addIngredient();
        }
        /**
         * Checks if all editable fields are filled. Recommended to check before accessing product amount.
         * Marks an unfilled entry as not filled.
         * @return true, if all elements are filled. false, if at least one element is not filled.
         */
        public boolean isFilled(){
            boolean returnValue = false;
            returnValue |= ViewUtils.checkTextViewIsFilled(mRecipeNameText);
            returnValue |= ViewUtils.checkTextViewIsFilled(mRecipeTagText);
            // TODO: CHECK Listview
            return !returnValue;
        }


        /**
         * Assigns all related references to the single view components.
         */
        private void assignIds(){
            mRecipeNameText = (EditText) mView.findViewById(R.id.fragment_recipe_details_recipe_name);
            mRecipeTagText  = (EditText) mView.findViewById(R.id.fragment_recipe_details_recipe_tags);
            mIngredientListView = (ListView) mView.findViewById(R.id.fragment_recipe_details_ingredient_container);
        }

    }
}

