package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 23.05.2015.
 */
public class IngredientListAdapter extends ArrayAdapter<Ingredient> {

    private List<Ingredient> mIngredientList;
    private List<Ingredient> mRemovedIngredients;

    private Activity mContext;

    public IngredientListAdapter(Activity _Context, List<Ingredient> _IngredientList){
        super(_Context, R.layout.list_recipe_ingredient_entry  , _IngredientList);
        mIngredientList = _IngredientList;
        mRemovedIngredients = new ArrayList<>();
        mContext = _Context;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;
        Ingredient ingredientEntry      = mIngredientList.get(_Position);

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(R.layout.list_recipe_ingredient_entry, null);
        }else{
            view = _ConvertView;
        }

        TextView amountTextView     = (TextView) view.findViewById(R.id.list_recipe_ingredient_entry_amount);
        TextView amountTypeTextView = (TextView) view.findViewById(R.id.list_recipe_ingredient_entry_amount_type);
        TextView nameTextView       = (TextView) view.findViewById(R.id.list_recipe_ingredient_entry_name);

        if(ingredientEntry.mProduct.mUnit != null) {
            amountTypeTextView.setText(ingredientEntry.mProduct.mUnit.mName);
        }
        amountTextView.setText(String.valueOf(ingredientEntry.mAmount));
        nameTextView.setText(ingredientEntry.mProduct.mName);

        return view;
    }

    public void addIngredient(Ingredient _Ingredient) {
        mIngredientList.add(_Ingredient);
        notifyDataSetChanged();
    }

    public void removeIngredient(Ingredient _Ingredient){
        int index = mIngredientList.indexOf(_Ingredient);
        Ingredient ingredient = mIngredientList.get(index);
        if(ingredient != null){
            mRemovedIngredients.add(ingredient);
        }

        mIngredientList.remove(index);
        notifyDataSetChanged();
    }

    public List<Ingredient> getIngredients(){
        return mIngredientList;
    }

    public List<Ingredient> getRemovedIngredients(){
        return mRemovedIngredients;
    }

    public void setData(List<Ingredient> _Ingredients, List<Ingredient> _RemovedIngredients) {
        this.mIngredientList = _Ingredients;
        this.mRemovedIngredients = _RemovedIngredients;
        notifyDataSetChanged();
    }
}
