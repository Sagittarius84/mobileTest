package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.view.MainShoppingListView;
import org.noorganization.instalist.view.customview.AmountPicker;
import org.noorganization.instalist.view.datahandler.RecipeDataHolder;
import org.noorganization.instalist.view.fragment.IngredientCreationFragment;
import org.noorganization.instalist.view.interfaces.IBaseActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TS on 23.05.2015.
 */
public class IngredientListAdapter implements ListAdapter {

    private List<Ingredient>      mUnderlyingIngredients;
    private List<DataSetObserver> mObservers;
    private static final int TYPEID_INGREDIENT = 1;
    private static final int TYPEID_BUTTON_BAR = 2;

    private Context mContext;

    public IngredientListAdapter(Context _context) {
        mUnderlyingIngredients = new ArrayList<>();
        mObservers = new LinkedList<>();

        mContext = _context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver _dataSetObserver) {
        mObservers.add(_dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver _dataSetObserver) {
        mObservers.remove(_dataSetObserver);
    }

    @Override
    public int getCount() {
        return mUnderlyingIngredients.size() + 1;
    }

    @Override
    public Object getItem(int _position) {
        if (_position >= mUnderlyingIngredients.size()) {
            return "data";
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
        View rtn;
        Log.d("Ingredient", "Wanted view for position " + _position);
        if (_position >= mUnderlyingIngredients.size()) {
            rtn = new TextView(mContext);
            ((TextView) rtn).setText("Just a dummy");
        } else {
            /*Ingredient current = mUnderlyingIngredients.get(_position);

            if (_viewToRecycle == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                rtn = inflater.inflate(R.layout.entry_ingredient, _parent);
            } else {
                rtn = _viewToRecycle;
            }

            AmountPicker picker = ((AmountPicker) rtn.findViewById(R.id.entry_ingredient_amount));
            picker.setValue(current.mAmount);
            picker.setStep(current.mProduct.mStepAmount);

            TextView productLabel = ((TextView) rtn.findViewById(R.id.entry_ingredient_product));
            productLabel.setText(current.mProduct.mName);

            */
            rtn = new TextView(mContext);
            ((TextView) rtn).setText("Just another dummy");
        }
        return rtn;
    }

    @Override
    public int getItemViewType(int _position) {
        return (mUnderlyingIngredients.size() > _position ? TYPEID_INGREDIENT : TYPEID_BUTTON_BAR);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int _position) {
        return true;
    }

    private class IngredientOnLongClickListener implements View.OnLongClickListener
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
    }
}
