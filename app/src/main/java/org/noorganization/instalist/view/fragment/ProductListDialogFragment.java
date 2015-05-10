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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.view.MainShoppingListView;
import org.noorganization.instalist.view.datahandler.SelectedProductDataHandler;
import org.noorganization.instalist.view.listadapter.SelectableProductListAdapter;

/**
 * Created by TS on 10.05.2015.
 * Responsible to show a dialog with a list of selectable products to add them to an existing shopping
 * list.
 */
public class ProductListDialogFragment extends DialogFragment{

    private Context mParentContext;

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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String listName = getArguments().getString("listName");
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
            }
        });
        adb.setNegativeButton(getString(R.string.product_list_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        adb.setTitle(getString(R.string.product_list_dialog_title) + " " + listName);

        return adb.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        //SelectedProductDataHandler.getInstance().setListEntries();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
      //  SelectedProductDataHandler.getInstance().clearListEntries();
    }
}
