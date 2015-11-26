package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.touchlistener.IOnShoppingListClickListenerEvents;
import org.noorganization.instalist.view.touchlistener.OnShoppingListClickListener;
import org.noorganization.instalist.view.interfaces.IShoppingListAdapter;

import java.util.List;

/**
 * The adapter handles the display of a single list of shoppinglists in the sidebar.
 * Should only be used when there is no Category.
 * Created by TS on 25.04.2015.
 */


public class PlainShoppingListOverviewAdapter extends ArrayAdapter<ShoppingList> implements IShoppingListAdapter{


    private static String LOG_TAG = PlainShoppingListOverviewAdapter.class.getName();

    private final List<ShoppingList> mShoppingLists;

    private final Context                            mContext;
    private       IOnShoppingListClickListenerEvents mIOnShoppingListClickEvents;

    private       IListController mListController;

    public PlainShoppingListOverviewAdapter(Context _Context, List<ShoppingList> _ListOfShoppingLists) {

        super(_Context, R.layout.expandable_list_view_list_entry, _ListOfShoppingLists);
        this.mContext = _Context;
        this.mShoppingLists = _ListOfShoppingLists;
        try {
            mIOnShoppingListClickEvents = (IOnShoppingListClickListenerEvents) _Context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Context.toString()
                    + " has no IOnShoppingListClickListenerEvents interface implemented.");
        }
        mListController = ControllerFactory.getListController(mContext);
    }

    private static class ViewHolder {
        TextView mtvListName;
        TextView mtvListItemCount;
    }

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View shoppingListNamesView = null;

        if (_ConvertView == null) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater shoppingListNamesInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            shoppingListNamesView = shoppingListNamesInflater.inflate(R.layout.expandable_list_view_list_entry, null);
            holder.mtvListName      = (TextView) shoppingListNamesView.findViewById(R.id.expandable_list_view_list_name);
            holder.mtvListItemCount = (TextView) shoppingListNamesView.findViewById(R.id.expandable_list_view_list_entries);
            shoppingListNamesView.setLongClickable(true);
            shoppingListNamesView.setTag(holder);
        } else {
            shoppingListNamesView = _ConvertView;
        }

        ViewHolder holder = (ViewHolder) shoppingListNamesView.getTag();
        ShoppingList shoppingList = getItem(_Position);
        String listName = shoppingList.mName;
        holder.mtvListName.setText(listName);

        holder.mtvListName.setSelected(true);
        holder.mtvListItemCount.setText(String.valueOf(mListController.getEntryCount(shoppingList)));

        shoppingListNamesView.setOnClickListener(
                new OnShoppingListClickListener(mIOnShoppingListClickEvents, mShoppingLists.get(_Position)));

        return shoppingListNamesView;

    }

    public void notifyUpadateListeners() {
        notifyDataSetChanged();

    }

    @Override
    public void addList(ShoppingList _ShoppingList) {
        mShoppingLists.add(_ShoppingList);
        notifyDataSetChanged();

    }

    @Override
    public void updateList(ShoppingList _ShoppingList) {
        int index = indexOfShoppingList(_ShoppingList);
        if (index < 0) {
            return;
        }
        mShoppingLists.set(index, _ShoppingList);
        notifyDataSetChanged();
    }

    @Override
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
        int indexOfList = -1;
        for (int index = 0; index < mShoppingLists.size(); ++index) {
            if (_ShoppingList.mUUID.equals(mShoppingLists.get(index).mUUID)) {
                indexOfList = index;
                break;
            }
        }
        return indexOfList;
    }
}
