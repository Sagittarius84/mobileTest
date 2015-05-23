package org.noorganization.instalist.view.fragment;

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
public class ProductListDialogFragment extends BaseCustomFragment{

    private ShoppingList mCurrentShoppingList;
    private String       mCurrentListName;


    private Button mAddNewProductButton;
    private Button mCancelButton;
    private Button mAddProductsButton;

    /**
     * Creates an instance of an ProductListDialogFragment.
     * @param _ListName the name of the list where the products should be added.
     * @return the new instance of this fragment.
     */
    public static ProductListDialogFragment newInstance(String _ListName){
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putString(MainShoppingListView.KEY_LISTNAME, _ListName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get bundle args to get the listname that should be shown
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }
        mCurrentListName    = bundle.getString(MainShoppingListView.KEY_LISTNAME);
        mCurrentShoppingList = ShoppingList.find(ShoppingList.class, ShoppingList.LIST_NAME_ATTR + "=?", mCurrentListName).get(0);
    }

    @Override
    public void onActivityCreated(Bundle _SavedIndstance) {
        super.onActivityCreated(_SavedIndstance);

        setToolbarTitle(mActivity.getResources().getText(R.string.product_list_dialog_title).toString());
        lockDrawerLayoutClosed();

        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_18dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ListAdapter adapter;
        View view = inflater.inflate(R.layout.fragment_product_list_dialog, container, false);

        List<Product> productList = Product.listAll(Product.class);

        List<ListEntry> listEntries = mCurrentShoppingList.getEntries();
        // remove all inserted list entries
        for(ListEntry listEntry : listEntries){
            productList.remove(listEntry.mProduct);
        }

        adapter = new SelectableProductListAdapter(getActivity(), productList, mCurrentShoppingList);

        mAddNewProductButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_new_product);
        mCancelButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_cancel);
        mAddProductsButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_products_to_list);

        TextView headingText        = (TextView) view.findViewById(R.id.fragment_product_list_dialog_list_name);
        ListView listView           = (ListView) view.findViewById(R.id.fragment_product_list_dialog_product_list_view);

        listView.setAdapter(adapter);
        headingText.setText(mActivity.getResources().getString(R.string.product_list_dialog_title) + " " + mCurrentShoppingList.mName);

        mAddNewProductButton.setOnClickListener(onAddNewProductClickListener);
        mCancelButton.setOnClickListener(onCancelClickListener);
        mAddProductsButton.setOnClickListener(onAddProductsClickListener);
        Button testRecipeButton = (Button) view.findViewById(R.id.testRecipeButton);
        testRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(RecipeCreationFragment.newInstance(mCurrentShoppingList.mName));
            }
        });
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
            changeFragment(ShoppingListOverviewFragment.newInstance(mCurrentListName));
        }
    };

    /**
     * Assign to go back to the last fragment.
     */
    private View.OnClickListener onCancelClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /**
     * Assign to call add new product overview.
     */
    private View.OnClickListener onAddNewProductClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            ProductCreationFragment creationFragment = ProductCreationFragment.newInstance(mCurrentShoppingList.mName);
            changeFragment(creationFragment);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mAddNewProductButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
        mAddProductsButton.setOnClickListener(null);
    }
}
