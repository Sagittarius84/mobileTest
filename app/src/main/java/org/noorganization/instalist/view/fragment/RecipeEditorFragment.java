package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IRecipeController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.view.activity.RecipeChangeActivity;
import org.noorganization.instalist.view.event.ProductSelectMessage;
import org.noorganization.instalist.view.listadapter.IngredientListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * This fragment is an editor for recipes. It can create new recipes but does not add them to a
 * list. Also it can change recipes. But list's then don't get modified i.e. to see the change on a
 * list the recipe has to get added first.
 * Created by michi on 01.07.15.
 */
public class RecipeEditorFragment extends Fragment {

    private static final String BK_EDITOR_MODE = "editorMode";
    private static final String BK_RECIPE_ID = "recipeId";
    private static final String BK_ADD_PRODUCTS = "productIds";
    private static final String BK_ADD_AMOUNTS = "productAmounts";

    private static final int EDITOR_MODE_CREATE = 1;
    private static final int EDITOR_MODE_EDIT = 2;

    private Recipe mRecipe;

    private ListView mIngredients;
    private EditText mRecipeName;
    private Button mAddIngredient;
    private Button mSave;
    private IngredientListAdapter mIngredientAdapter;

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

    public static RecipeEditorFragment newUpdateInstance(long _recipeId) {
        RecipeEditorFragment rtn = new RecipeEditorFragment();
        Bundle parameters = new Bundle();
        parameters.putInt(BK_EDITOR_MODE, EDITOR_MODE_EDIT);
        parameters.putLong(BK_RECIPE_ID, _recipeId);

        List<Ingredient> currentIngredients = SugarRecord.findById(Recipe.class, _recipeId).
                getIngredients();

        long resultingIds[] = new long[currentIngredients.size()];
        float resultingAmounts[] = new float[currentIngredients.size()];
        int convertIndex = 0;
        for (Ingredient currentIngredient : currentIngredients) {
            resultingIds[convertIndex] = currentIngredient.mProduct.getId();
            resultingAmounts[convertIndex] = currentIngredient.mAmount;
            convertIndex++;
        }

        parameters.putLongArray(BK_ADD_PRODUCTS, resultingIds);
        parameters.putFloatArray(BK_ADD_AMOUNTS, resultingAmounts);

        rtn.setArguments(parameters);
        return rtn;
    }

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        Bundle parameters = getArguments();
        ActionBar supportActionBar = ((RecipeChangeActivity) getActivity()).getSupportActionBar();

        if (parameters != null) {

            String title = "";

            if (parameters.getInt(BK_EDITOR_MODE) == EDITOR_MODE_EDIT) {
                mRecipe = SugarRecord.findById(Recipe.class, parameters.getLong(BK_RECIPE_ID));
                title = getString(R.string.edit_recipe);
            } else {
                title = getString(R.string.create_recipe);
            }
            if (supportActionBar != null) {
                supportActionBar.setTitle(title);
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater _inflater, ViewGroup _parent, Bundle _savedInstanceState) {
        View mainView = _inflater.inflate(R.layout.fragment_recipe_details, _parent, false);

        mIngredients = (ListView) mainView.findViewById(R.id.fragment_recipe_details_ingredients);
        View actions = _inflater.inflate(R.layout.fragment_recipe_details_actions, null);
        mIngredients.addFooterView(actions);

        mAddIngredient = (Button) actions.findViewById(R.id.fragment_recipe_details_add_ingredient);
        mSave = (Button) actions.findViewById(R.id.fragment_recipe_details_save);
        mRecipeName = (EditText) mainView.findViewById(R.id.fragment_recipe_details_name);

        fillViews();
        addArgIngredients();

        return mainView;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * EventBus-receiver for selections made via ProductListDialogFragment.
     */
    public void onEvent(ProductSelectMessage _selectedProducts) {
        long previousIds[] = getArguments().getLongArray(BK_ADD_PRODUCTS);
        float previousAmounts[] = getArguments().getFloatArray(BK_ADD_AMOUNTS);

        ArrayList<Long> newProductIds = new ArrayList<>();
        ArrayList<Float> newAmounts = new ArrayList<>();

        if (previousIds != null && previousAmounts != null) {
            for (int convertIndex = 0; convertIndex < previousIds.length; convertIndex++) {
                newProductIds.add(previousIds[convertIndex]);
                newAmounts.add(previousAmounts[convertIndex]);
            }
        }

        for (Product currentProduct : _selectedProducts.mProducts.keySet()) {
            float amount = _selectedProducts.mProducts.get(currentProduct);
            if (newProductIds.contains(currentProduct.getId())) {
                int position = newProductIds.indexOf(currentProduct.getId());
                newAmounts.set(position, newAmounts.get(position) + amount);
            } else {
                newProductIds.add(currentProduct.getId());
                newAmounts.add(amount);
            }

            mIngredientAdapter.addIngredient(new Ingredient(currentProduct, null, amount));
        }

        long resultingIds[] = new long[newProductIds.size()];
        float resultingAmounts[] = new float[newProductIds.size()];
        for (int convertIndex = 0; convertIndex < resultingIds.length; convertIndex++) {
            resultingIds[convertIndex] = newProductIds.get(convertIndex);
            resultingAmounts[convertIndex] = newAmounts.get(convertIndex);
        }

        getArguments().putLongArray(BK_ADD_PRODUCTS, resultingIds);
        getArguments().putFloatArray(BK_ADD_AMOUNTS, resultingAmounts);
    }

    @Override
    public void onPause() {

        List<Ingredient> currentIngredients = mIngredientAdapter.getIngredients();

        long resultingIds[] = new long[currentIngredients.size()];
        float resultingAmounts[] = new float[currentIngredients.size()];
        int convertIndex = 0;
        for (Ingredient currentIngredient : currentIngredients) {
            resultingIds[convertIndex] = currentIngredient.mProduct.getId();
            resultingAmounts[convertIndex] = currentIngredient.mAmount;
            convertIndex++;
        }

        getArguments().putLongArray(BK_ADD_PRODUCTS, resultingIds);
        getArguments().putFloatArray(BK_ADD_AMOUNTS, resultingAmounts);

        super.onPause();
    }

    private void addArgIngredients() {
        long productIds[] = getArguments().getLongArray(BK_ADD_PRODUCTS);
        float amounts[] = getArguments().getFloatArray(BK_ADD_AMOUNTS);
        if (productIds == null || amounts == null || productIds.length != amounts.length) {
            return;
        }

        for (int currentIndex = 0; currentIndex < productIds.length; currentIndex++) {
            Ingredient toAdd = new Ingredient(SugarRecord.findById(Product.class, productIds[currentIndex]),
                    null,
                    amounts[currentIndex]);
            mIngredientAdapter.addIngredient(toAdd);
        }
    }

    private void fillViews() {
        List<Ingredient> ingredientList = new ArrayList<>();
        if (mRecipe != null) {
            mRecipeName.setText(mRecipe.mName);
        }

        mIngredientAdapter = new IngredientListAdapter(getActivity(), ingredientList);
        mIngredients.setAdapter(mIngredientAdapter);

        mSave.setOnClickListener(new OnSaveListener());
        mAddIngredient.setOnClickListener(new OnAddIngredientListener());
    }

    private boolean validate() {
        boolean rtn = true;
        if (mIngredientAdapter.getIngredients().size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.no_ingredients), Toast.LENGTH_LONG).show();
            rtn = false;
        }

        String newName = mRecipeName.getText().toString();

        // no title for recipe
        if (newName.length() == 0) {
            mRecipeName.setError(getString(R.string.error_no_input));
            rtn = false;
        } else {
            // title for recipe is set
            List<Recipe> recipesToValidate = Select.from(Recipe.class).
                    where(Condition.prop(Recipe.ATTR_NAME).eq(newName)).list();

            if (recipesToValidate.size() != 0 && (mRecipe == null || !mRecipe.getId().equals(recipesToValidate.get(0).getId()))) {
                // found elements that matches new name and recipe is new or recipe name is changed for another recipe
                mRecipeName.setError(getString(R.string.name_exists));
                rtn = false;
            }

        }

        return rtn;
    }

    private class OnAddIngredientListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ViewUtils.addFragment(getActivity(), ProductListDialogFragment.newInstance(false));
        }
    }

    private class OnSaveListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (validate()) {
                IRecipeController controller = ControllerFactory.getRecipeController();

                String newRecipeName = mRecipeName.getText().toString();
                if (mRecipe == null) {
                    mRecipe = controller.createRecipe(newRecipeName);
                } else {
                    mRecipe = controller.renameRecipe(mRecipe, newRecipeName);
                }

                if (mRecipe == null) {
                    Toast.makeText(getActivity(), getString(R.string.error_recipe_creation), Toast.LENGTH_LONG).show();
                    return;
                }

                for (Ingredient toDelete : mIngredientAdapter.getDeleted()) {
                    controller.removeIngredient(mRecipe, toDelete.mProduct);
                }

                for (Ingredient toSave : mIngredientAdapter.getIngredients()) {
                    controller.addOrChangeIngredient(mRecipe, toSave.mProduct, toSave.mAmount);
                }

                // ViewUtils.removeFragment(getActivity(), RecipeEditorFragment.this); // to much
                getActivity().finish();
            }
        }
    }
}
