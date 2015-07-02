package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.orm.SugarRecord;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.view.listadapter.IngredientListAdapter;

/**
 * Created by michi on 01.07.15.
 */
public class RecipeEditorFragment extends Fragment {

    private static final String BK_EDITOR_MODE = "editorMode";
    private static final String BK_RECIPE_ID   = "recipeId";

    private static final int    EDITOR_MODE_CREATE = 1;
    private static final int    EDITOR_MODE_EDIT   = 2;

    private Recipe     mRecipe;
    private ViewHolder mViews;

    /**
     * The default constructor needed by the FragmentManager. Use the newXXXInstance for creation
     * instead.
     */
    public RecipeEditorFragment() {
    }

    public static RecipeEditorFragment newCreationInstance() {
        RecipeEditorFragment rtn = new RecipeEditorFragment();
        Bundle parameters = new Bundle();
        parameters.putInt(BK_EDITOR_MODE, EDITOR_MODE_CREATE);
        rtn.setArguments(parameters);
        return rtn;
    }

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        Bundle parameters = getArguments();
        if (parameters != null) {
            if (parameters.getInt(BK_EDITOR_MODE) == EDITOR_MODE_EDIT) {
                mRecipe = SugarRecord.findById(Recipe.class, parameters.getLong(BK_RECIPE_ID));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup _parent, Bundle _savedInstanceState) {
        View mainView = _inflater.inflate(R.layout.fragment_recipe_details, _parent, false);

        mViews = new ViewHolder(mainView);

        return mainView;
    }

    private class ViewHolder {
        private View     mParent;
        private EditText mTitleInput;
        private Button   mAddIngredientInput;
        private Button   mSaveInput;
        private ListView mIngredientsInput;

        public ViewHolder(View _parent) {
            mParent = _parent;
            init();
        }

        private void init() {
            mTitleInput = (EditText) mParent.findViewById(R.id.fragment_recipe_details_name);
            mIngredientsInput = (ListView) mParent.findViewById(R.id.fragment_recipe_details_ingredients);
            mIngredientsInput.setAdapter(new IngredientListAdapter(getActivity()));
        }
    }
}
