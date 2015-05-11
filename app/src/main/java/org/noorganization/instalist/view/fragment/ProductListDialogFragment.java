package org.noorganization.instalist.view.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
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
public class ProductListDialogFragment extends DialogFragment{

    private Context mParentContext;
    private String mListName;
    /**
     * Creates an instance of an ProductListDialogFragment.
     * @param listName the name of the list where the products should be added.
     * @return the new instance of this fragment.
     */
    public static ProductListDialogFragment newInstance(String listName){
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putString("listName", listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListName = getArguments().getString("listName");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListAdapter adapater;
        AlertDialog.Builder adb;

        // create alertdialog
        adb = new AlertDialog.Builder(getActivity());
        adapater = new SelectableProductListAdapter(getActivity(), Product.listAll(Product.class));

        adb.setAdapter(adapater, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adb.setPositiveButton(getString(R.string.product_list_dialog_add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((MainShoppingListView) getActivity()).addProductsToList();
                List<ListEntry> listEntries = SelectedProductDataHandler.getInstance().getListEntries();
                ShoppingList list = ShoppingList.find(ShoppingList.class, ShoppingList.LIST_NAME_ATTR + "=?", mListName).get(0);
                IListController mListController = ListController.getInstance();

                for(ListEntry listEntry : listEntries){
                    if(listEntry.mStruck){
                        ListEntry listEntryIntern = mListController.addOrChangeItem(list, listEntry.mProduct, 1.0f);
                        if(listEntryIntern == null){
                            Log.e(ProductListDialogFragment.class.getName(), "Insertion failed.");
                        }
                    }
                }
                ShoppingList shopList = ShoppingList.findById(ShoppingList.class,list.getId());
                List<ListEntry> entire = shopList.getEntries();
                entire.get(0);
            }
        });
        adb.setNegativeButton(getString(R.string.product_list_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // getFragmentManager().beginTransaction().replace(R.id.container, new ProductCreationFragment()).addToBackStack(null).commit();
            }
        });
        adb.setTitle(getString(R.string.product_list_dialog_title) + " " + mListName);

        return adb.create();
    }
}
