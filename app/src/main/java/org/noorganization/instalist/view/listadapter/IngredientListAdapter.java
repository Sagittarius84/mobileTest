package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;

import java.util.List;

/**
 * Created by TS on 23.05.2015.
 */
public class IngredientListAdapter extends ArrayAdapter<Ingredient> {

    private List<Ingredient> mIngredientList;
    private Activity mContext;

    public IngredientListAdapter(Activity _Context, List<Ingredient> _IngredientList){
        super(_Context, R.layout.list_selectable_product  , _IngredientList);
        mIngredientList = _IngredientList;
        mContext = _Context;
    }



    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;
        Ingredient ingredientEntry      = mIngredientList.get(_Position);

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            view = shoppingListNamesInflater.inflate(R.layout.list_selectable_product, null);
        }else{
            view = _ConvertView;
        }

        TextView textView       = (TextView) view.findViewById(R.id.product_list_product_name);
        CheckBox checkBox       = (CheckBox) view.findViewById(R.id.product_list_product_selected);
        textView.setText(ingredientEntry.mProduct.mName);
        return view;
    }

    public void addIngredient() {
        Product product = new Product("Testtestetests", null, 1.0f, 1.0f);
        Recipe recipe = new Recipe("Schalom");
        Ingredient ing = new Ingredient(product, recipe, 1.0f);
        mIngredientList.add(ing);
        notifyDataSetChanged();
    }
}
