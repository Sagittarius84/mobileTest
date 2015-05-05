package org.noorganization.instalist.view.ListAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.view.MainShoppingListView;

import java.util.List;

/**
 * An adapter that handles the display of the names of a shopping list.
 * Created by TS on 25.04.2015.
 */
public class ShoppingListOverviewAdapter extends ArrayAdapter<String> {

    private static String LOG_TAG = ShoppingListOverviewAdapter.class.getName();

    private final List<String> mListOfListNames;
    private final Activity mContext;

    public ShoppingListOverviewAdapter(Activity _Context, List<String> _ListOfListNames){

        super(_Context, android.R.layout.simple_list_item_1, _ListOfListNames);
        this.mContext = _Context;
        this.mListOfListNames = _ListOfListNames;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View shoppingListNamesView = null;

        if(_ConvertView == null){
            LayoutInflater shoppingListNamesInflater = mContext.getLayoutInflater();
            shoppingListNamesView = shoppingListNamesInflater.inflate(android.R.layout.simple_list_item_1, null);
        }else{
            shoppingListNamesView = _ConvertView;
        }

        String listName     = mListOfListNames.get(_Position);
        TextView textView   = (TextView) shoppingListNamesView.findViewById(android.R.id.text1);
        textView.setText(listName);

        shoppingListNamesView.setOnClickListener( new OnListNameClickListener(listName));

        return shoppingListNamesView;
    }

    /**
     * On click listener for managing the on click events on a shoppinglistname
     */
    private class OnListNameClickListener implements View.OnClickListener{

        private String mListName;

        public OnListNameClickListener(String _ListName){
            mListName = _ListName;
        }
        @Override
        public void onClick(View view) {
            ((MainShoppingListView) mContext).selectList(mListName);
        }
    }
}

