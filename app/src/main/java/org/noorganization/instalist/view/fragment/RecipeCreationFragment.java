package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.noorganization.instalist.R;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by TS on 28.04.2015.
 */
public class RecipeCreationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        return view;
    }


    private static class ViewAcccessor{

        private EditText    mRecipeNameText;
        private ListView    mIngredientListView;
        private EditText    mRecipeTagText;
        private View        mView;

        /**
         * Initializes all views and sets the view reference.
         * @param _View the view that is currently used.
         */
        public ViewAcccessor(View _View){
            this.mView = _View;
            assignIds();
        }

        /**
         * Initializes all views and sets the view references. Also it sets all by the user editable fields.
         * @param _View the view that is currently used.
         * @param _RecipeName the recipe name that should be filled in.
         * @param _RecipeTags the tags which belongs to the Recipe
         * @param _ListOfIngredients the list of Ingredients
         *                           TODO: Check if usage of recipe as parameter would be better
         */
        public ViewAcccessor(View _View, String _RecipeName, String _RecipeTags, ArrayList<String> _ListOfIngredients){

            this.mView = _View;
            assignIds();

            mRecipeNameText.setText(_RecipeName);
            mRecipeTagText.setText(_RecipeTags);
            // TODO: own listadapter
            mIngredientListView.setAdapter(new ArrayAdapter<String>(mView.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, _ListOfIngredients));
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

