package org.noorganization.instalist.view.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filterable;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.view.modelwrappers.IBaseListEntry;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by tinos_000 on 22.07.2015.
 */
public class SelectableItemListAdapter2 extends ArrayAdapter<IBaseListEntry> implements Filterable {

    //region Private Attributes
    /**
     * The context of the app.
     */
    private Context mContext;

    /**
     * The Ressource id of the layout of a single item.
     */
    private int mLayoutId;
    /**
     * Entries that are marked as checked.
     */
    private Set<IBaseListEntry> mCheckedEntries;

    /**
     * The Listentries for the list.
     */
    private List<IBaseListEntry> mListEntries;

    // Some thoughts about instance of usage
    //  It will affect it really negative when sorting happens, then all elements will be checked and this will result in a great performance loss.
    // usage of instance of is no bottleneck for this case, it will be called at max 30 times when first displayed or change was notified.
    // it depends on the screen size. But as stated in some measurements on this entry http://stackoverflow.com/a/26514984/2980948
    // it is save to work with with such a little dataset.
    // private ArrayList<Object> mListEntries;

    /**
     * The local comparator to be used for ordering the list.
     */
    private Comparator<IBaseListEntry> mComparator;

    //endregion

    //region Constructor

    /**
     * Default Constructor. Sets the comparator to sort by name.
     * @param _Context the context of the activity.
     * @param _Resource the resource id for the custom listview.
     * @param _ListItems the items which should be displayed. They must implement IBaseListEntry.
     */
    public SelectableItemListAdapter2(Context _Context, int _Resource, List<IBaseListEntry> _ListItems) {
        super(_Context, _Resource, _ListItems);
        initialize(_Context, _Resource, _ListItems);

        // TODO: set comparator mComparator;
    }

    /**
     * Constructor to modify comparator.
     * @param _Context the context of the activity.
     * @param _Resource the resource id for the custom listview.
     * @param _ListItems the items which should be displayed. They must implement IBaseListEntry.
     */
    public SelectableItemListAdapter2(Context _Context, int _Resource, List<IBaseListEntry> _ListItems, Comparator<IBaseListEntry> _Comparator) {
        super(_Context, _Resource, _ListItems);
        initialize(_Context, _Resource, _ListItems);
        mComparator = _Comparator;
    }

    /**
     * Does the common initialization.
     */
    private void initialize(Context _Context, int _Resource, List<IBaseListEntry> _ListItems){
        mContext = _Context;
        mListEntries = _ListItems;
        mLayoutId = _Resource;
        // used this value out of space, maybe choose a little bit lower.
        mCheckedEntries = new HashSet<>((int) (mListEntries.size() * 0.6f));
    }

    //endregion


    //region Adapter Methods

    @Override
    public View getView(int _Position, View _ConvertView, ViewGroup _Parent) {
        View view = null;
        IBaseListEntry thisEntry = mListEntries.get(_Position);

        // TODO: Viewholder
        if(_ConvertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(mLayoutId, null);
        }else{
            view = _ConvertView;
        }

        TextView textView       = (TextView) view.findViewById(R.id.product_list_product_name);
        CheckBox checkBox       = (CheckBox) view.findViewById(R.id.product_list_product_selected);

        textView.setText(thisEntry.getName());
        checkBox.setChecked(mCheckedEntries.contains(thisEntry));

        // view.setOnClickListener(new OnClickListenerListEntry(listEntry));
        // view.setOnLongClickListener(new OnLongClickListenerListEntry(listEntry.getItemListEntry()));

        return view;
    }

    //endregion

    //region Public Access
    public void addItem(IBaseListEntry _ListEntry){

    }

    public IBaseListEntry getItem(int _Position){
        return null;
    }

    /**
     * Returns the iterator of the list of checked entries.
     * @return iterator of List.
     */
    public Iterator<IBaseListEntry> getCheckedListEntries(){
        return mCheckedEntries.iterator();
    }
    //endregion
}
