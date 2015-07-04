package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.view.customview.AmountPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * The adapter displays some ingredients for editing recipes.
 */
public class IngredientListAdapter extends ArrayAdapter<Ingredient> {

    private List<Ingredient> mUnderlyingIngredients;
    private List<Ingredient> mRemovedIngredients;

    private Context mContext;

    public IngredientListAdapter(Context _context, List<Ingredient> _ingredients) {
        super(_context, R.layout.entry_ingredient, _ingredients);
        mUnderlyingIngredients = _ingredients;
        mRemovedIngredients = new ArrayList<>();

        mContext = _context;
    }

    public void addIngredient(Ingredient _newIngredient) {
        mUnderlyingIngredients.add(_newIngredient);
        notifyDataSetChanged();
    }

    public List<Ingredient> getIngredients() {
        return mUnderlyingIngredients;
    }

    public List<Ingredient> getDeleted() {
        return mRemovedIngredients;
    }

    @Override
    public Ingredient getItem(int _position) {
        if (_position >= mUnderlyingIngredients.size()) {
            return null;
        }

        return mUnderlyingIngredients.get(_position);
    }

    @Override
    public long getItemId(int _position) {
        if (_position < mUnderlyingIngredients.size()) {
            return mUnderlyingIngredients.get(_position).mProduct.getId();
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int _position, View _viewToRecycle, ViewGroup _parent) {
        View rtn = _viewToRecycle;
        if (rtn == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            rtn = inflater.inflate(R.layout.entry_ingredient, null);
        }

        Ingredient current = mUnderlyingIngredients.get(_position);

        AmountPicker picker = ((AmountPicker) rtn.findViewById(R.id.entry_ingredient_amount));
        picker.setTag(_position);
        picker.setValue(current.mAmount);
        picker.setStep(current.mProduct.mStepAmount);
        picker.setChangeListener(new IngredientAmountChangeListener());

        TextView productLabel = ((TextView) rtn.findViewById(R.id.entry_ingredient_product));
        productLabel.setText(current.mProduct.mName);

        return rtn;
    }

    private class IngredientAmountChangeListener implements AmountPicker.IValueChangeListener {

        @Override
        public void onValueChanged(AmountPicker _picker, float _newValue) {
            Log.d("vcl", "new value:" + _newValue);
            int position = (int) _picker.getTag();
            mUnderlyingIngredients.get(position).mAmount = _newValue;
        }
    }

    /*private class IngredientOnLongClickListener implements View.OnLongClickListener
    {
        private Ingredient mIngredient;

        public IngredientOnLongClickListener(Ingredient _Ingredient){
            mIngredient = _Ingredient;
        }

        @Override
        public boolean onLongClick(View v) {
            Fragment fragment;
            if(mIngredient.getId() != null) {
                fragment = IngredientCreationFragment.newInstance(mIngredient.getId());
            }else{
                int index = RecipeDataHolder.getInstance().getIngredients().indexOf(mIngredient);
                fragment = IngredientCreationFragment.newInstance(index);
            }
            ((MainShoppingListView) mContext).changeFragment(fragment);
            return true;
        }
    }*/
}
