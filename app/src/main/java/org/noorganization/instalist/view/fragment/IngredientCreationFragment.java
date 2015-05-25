package org.noorganization.instalist.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.view.datahandler.RecipeDataHolder;
import org.noorganization.instalist.view.spinneradapter.ProductSpinnerAdapter;

import java.util.List;

/**
 * Created by TS on 24.05.2015.
 */
public class IngredientCreationFragment extends BaseCustomFragment {

    private EditText mAmountEditText;
    private Spinner mIngredientSpinner;
    private Button mAddIngredientButton;
    private Button mCancelButton;

    private List<Product> mListOfProducts;

    public static IngredientCreationFragment newInstance(){
        return new IngredientCreationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListOfProducts = Product.listAll(Product.class);
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

        mIngredientSpinner.setAdapter(new ProductSpinnerAdapter(mActivity, mListOfProducts));

        mAddIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Ingredient> ingredients = RecipeDataHolder.getInstance().getIngredients();
                Ingredient ingredient = new Ingredient();
                ingredient.mAmount = Float.valueOf(mAmountEditText.getText().toString());
                ingredient.mProduct = (Product) mIngredientSpinner.getSelectedItem();
                ingredients.add(ingredient);
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

}
