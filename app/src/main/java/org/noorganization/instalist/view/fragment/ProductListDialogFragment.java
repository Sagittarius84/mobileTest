package org.noorganization.instalist.view.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.controller.implementation.ListController;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.MainShoppingListView;
import org.noorganization.instalist.view.datahandler.SelectedProductDataHandler;
import org.noorganization.instalist.view.listadapter.SelectableProductListAdapter;

import java.util.List;

/**
 * Created by TS on 10.05.2015.
 * Responsible to show a dialog with a list of selectable products to add them to an existing shopping
 * list.
 */
public class ProductListDialogFragment extends Fragment{

    private Context mParentContext;
    private ShoppingList mCurrentShoppingList;


    /**
     * Creates an instance of an ProductListDialogFragment.
     * @param _ListName the name of the list where the products should be added.
     * @return the new instance of this fragment.
     */
    public static ProductListDialogFragment newInstance(String _ListName){
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putString("listName", _ListName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.LIST_NAME_ATTR + "=?", getArguments().getString("listName")).get(0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListAdapter adapter;
        View view = inflater.inflate(R.layout.fragment_product_list_dialog, container, false);

        adapter = new SelectableProductListAdapter(getActivity(), Product.listAll(Product.class), mCurrentShoppingList);

        Button addNewProductButton  = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_new_product);
        Button cancelButton         = (Button) view.findViewById(R.id.fragment_product_list_dialog_cancel);
        Button addProductsButton    = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_products_to_list);

        TextView headingText        = (TextView) view.findViewById(R.id.fragment_product_list_dialog_list_name);
        ListView listView           = (ListView)view.findViewById(R.id.fragment_product_list_dialog_product_list_view);

        listView.setAdapter(adapter);
        headingText.setText(getString(R.string.product_list_dialog_title) + " " + mCurrentShoppingList.mName);

        addNewProductButton.setOnClickListener(onAddNewProductClickListener);
        cancelButton.setOnClickListener(onCancelClickListener);
        addProductsButton.setOnClickListener(onAddProductsClickListener);

        return view;
    }

    /**
     * Assign to add all selected list items to the current list.
     */
    private View.OnClickListener onAddProductsClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            ((MainShoppingListView) getActivity()).addProductsToList();
            List<ListEntry> listEntries = SelectedProductDataHandler.getInstance().getListEntries();
            IListController mListController = ControllerFactory.getListController();

            for(ListEntry listEntry : listEntries){
                if(listEntry.mStruck){
                    ListEntry listEntryIntern = mListController.addOrChangeItem(mCurrentShoppingList, listEntry.mProduct, 1.0f);
                    if(listEntryIntern == null){
                        Log.e(ProductListDialogFragment.class.getName(), "Insertion failed.");
                    }
                }
            }
            // go back to old fragment
            getFragmentManager().popBackStack();
        }
    };

    /**
     * Assign to go back to the last fragment.
     */
    private View.OnClickListener onCancelClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            getFragmentManager().popBackStack();
        }
    };

    /**
     * Assign to call add new product overview.
     */
    private View.OnClickListener onAddNewProductClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new ProductCreationFragment().newInstance(mCurrentShoppingList.mName))
                    .commit();
        }
    };
}
