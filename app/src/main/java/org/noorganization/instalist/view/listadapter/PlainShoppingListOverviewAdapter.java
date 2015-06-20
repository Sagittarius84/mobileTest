package org.noorganization.instalist.view.listadapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.touchlistener.IOnShoppingListClickListenerEvents;
import org.noorganization.instalist.touchlistener.OnShoppingListClickListener;

import java.util.List;

/**
 * The adapter handles the display of a single list of shoppinglists in the sidebar.
 * Should only be used when there is no Category.
 * Created by TS on 25.04.2015.
 */


public class PlainShoppingListOverviewAdapter extends ArrayAdapter<ShoppingList> {


    private static String LOG_TAG = PlainShoppingListOverviewAdapter.class.getName();

    private final List<ShoppingList> mShoppingLists;

    private final Activity                           mContext;
    private       IOnShoppingListClickListenerEvents mIOnShoppingListClickEvents;

    public PlainShoppingListOverviewAdapter(Activity _Context, List<ShoppingList> _ListOfShoppingLists) {

        super(_Context, android.R.layout.simple_list_item_1, _ListOfShoppingLists);
        this.mContext = _Context;
        this.mShoppingLists = _ListOfShoppingLists;
        try {
            mIOnShoppingListClickEvents = (IOnShoppingListClickListenerEvents) _Context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Context.toString()
                    + " has no IOnShoppingListClickListenerEvents interface implemented.");
        }
    }


    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View shoppingListNamesView = null;

        if (_ConvertView == null) {
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            shoppingListNamesView = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);

        } else {
            shoppingListNamesView = _ConvertView;

        }

        String   listName = mShoppingLists.get(_Position).mName;
        TextView textView = (TextView) shoppingListNamesView.findViewById(android.R.id.text1);
        textView.setText(listName);

        shoppingListNamesView.setOnClickListener(
                new OnShoppingListClickListener(mIOnShoppingListClickEvents, mShoppingLists.get(_Position)));

        return shoppingListNamesView;

    }

    public void notifyUpadateListeners() {
        notifyDataSetChanged();

    }

    public void addList(ShoppingList _ShoppingList) {
        mShoppingLists.add(_ShoppingList);
        notifyDataSetChanged();

    }

    public void changeList(ShoppingList _ShoppingList) {
        int index = indexOfShoppingList(_ShoppingList);
        if (index < 0) {
            return;
        }
        mShoppingLists.set(index, _ShoppingList);
        notifyDataSetChanged();
    }

    public void removeList(ShoppingList _ShoppingList) {
        int index = indexOfShoppingList(_ShoppingList);
        if (index < 0) {
            return;
        }
        mShoppingLists.remove(index);
        notifyDataSetChanged();
    }

    /**
     * Get the index of the given ShoppingList object in the PlainShoppingListOverviewAdapter list.
     *
     * @param _ShoppingList the list that should be found.
     * @return -1 if not found, index of item when found.
     */
    private int indexOfShoppingList(ShoppingList _ShoppingList) {
        int indexOfList = - 1;
        for (int index = 0; index < mShoppingLists.size(); ++ index) {
            if (_ShoppingList.getId() == mShoppingLists.get(index).getId()) {
                indexOfList = index;
                break;
            }
        }
        return indexOfList;
    }
}
