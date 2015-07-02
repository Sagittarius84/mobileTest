package org.noorganization.instalist.view.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.orm.SugarRecord;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.view.BaseItemListEntry;
import org.noorganization.instalist.model.view.ProductListEntry;
import org.noorganization.instalist.model.view.RecipeListEntry;
import org.noorganization.instalist.model.view.SelectableBaseItemListEntry;
import org.noorganization.instalist.view.activity.RecipeChangeActivity;
import org.noorganization.instalist.view.datahandler.SelectableBaseItemListEntryDataHolder;
import org.noorganization.instalist.view.interfaces.IBaseActivity;
import org.noorganization.instalist.view.listadapter.SelectableItemListAdapter;
import org.noorganization.instalist.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TS on 10.05.2015.
 * Responsible to show a dialog with a list of selectable products to add them to an existing shopping
 * list.
 */
public class ProductListDialogFragment extends Fragment{

    public static final String FILTER_BY_PRODUCT = "0";
    public static final String FILTER_BY_RECIPE = "1";
    public static final String FILTER_SHOW_ALL = "2";

    private ShoppingList mCurrentShoppingList;

    private Button mCreateProductButton;
    private Button mCancelButton;
    private Button mAddProductsButton;
    private Button mCreateRecipeButton;

    private static final String BUNDLE_KEY_LIST_ID = "ListId";

    // create the abstract selectable list entries to show mixed entries
    private List<SelectableBaseItemListEntry> mSelectableBaseItemListEntries = new ArrayList<>();
    private SelectableItemListAdapter mListAdapter;

    private IBaseActivity mBaseActivityInterface;
    private Context mContext;

    /**
     * Creates an instance of an ProductListDialogFragment.
     * @param _ListId the id of the list where the products should be added.
     * @return the new instance of this fragment.
     */
    public static ProductListDialogFragment newInstance(long _ListId){
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putLong(BUNDLE_KEY_LIST_ID, _ListId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity _Activity) {
        super.onAttach(_Activity);
        mContext = _Activity;
        try {
            mBaseActivityInterface = (IBaseActivity) _Activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(_Activity.toString()
                    + " has no IBaseActivity interface attached.");
        }
    }

    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        // get bundle args to get the listname that should be shown
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            return;
        }

        setHasOptionsMenu(true);
        long currentListId = bundle.getLong(BUNDLE_KEY_LIST_ID);
        mCurrentShoppingList = SugarRecord.findById(ShoppingList.class, currentListId);
        //String currentListName = bundle.getString(MainShoppingListView.KEY_LISTNAME);
        //mCurrentShoppingList = ShoppingList.findByName(currentListName);

        if(mCurrentShoppingList == null){
            throw new IllegalStateException(ProductListDialogFragment.class.toString() +
                    ": Cannot find corresponding ShoppingList for id: " + currentListId);
        }

        List<Product> productList = Product.listAll(Product.class);
        List<Recipe> recipeList = Recipe.listAll(Recipe.class);
        List<ListEntry> listEntries = mCurrentShoppingList.getEntries();

        // remove all inserted list entries
        for(ListEntry listEntry : listEntries){
            productList.remove(listEntry.mProduct);
        }

        for(Product product: productList){
            mSelectableBaseItemListEntries.add(new SelectableBaseItemListEntry(new ProductListEntry(product)));
        }

        for(Recipe recipe: recipeList){
            mSelectableBaseItemListEntries.add(new SelectableBaseItemListEntry(new RecipeListEntry(recipe)));
        }

        mAddProductsListener   = new OnAddProductsListener();
        mCancelListener        = new OnCancelListener();
        mCreateProductListener = new OnCreateProductListener();
    }

    @Override
    public void onActivityCreated(Bundle _SavedIndstance) {
        super.onActivityCreated(_SavedIndstance);

    }

    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        super.onCreateView(_Inflater, _Container, _SavedInstanceState);

        View view = _Inflater.inflate(R.layout.fragment_product_list_dialog, _Container, false);

        mListAdapter = new SelectableItemListAdapter(getActivity(), mSelectableBaseItemListEntries, mCurrentShoppingList);

        mCreateProductButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_new_product);
        mCancelButton           = (Button) view.findViewById(R.id.fragment_product_list_dialog_cancel);
        mAddProductsButton      = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_products_to_list);
        mCreateRecipeButton = (Button) view.findViewById(R.id.testRecipeButton);

        ListView listView           = (ListView) view.findViewById(R.id.fragment_product_list_dialog_product_list_view);

        listView.setAdapter(mListAdapter);

        mBaseActivityInterface.setToolbarTitle(mContext.getResources().getString(R.string.product_list_dialog_title) + " " + mCurrentShoppingList.mName);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_product_list_dialog, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_product_list_dialog_filter_by_product:
                mListAdapter.getFilter().filter(FILTER_BY_PRODUCT);
                break;
            case R.id.menu_product_list_dialog_filter_by_recipe:
                mListAdapter.getFilter().filter(FILTER_BY_RECIPE);
                break;
            case R.id.menu_product_list_dialog_filter_by_all:
                mListAdapter.getFilter().filter(FILTER_SHOW_ALL);
                break;
            case R.id.menu_product_list_dialog_sort_by_name:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBaseActivityInterface.setToolbarTitle(mContext.getResources().getString(R.string.product_list_dialog_title));
        mBaseActivityInterface.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mBaseActivityInterface.setNavigationIcon(R.mipmap.ic_arrow_back_white_36dp);
        mBaseActivityInterface.setNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivityInterface.onBackPressed();
            }
        });
        mCreateProductButton.setOnClickListener(mCreateProductListener);
        mCancelButton.setOnClickListener(mCancelListener);
        mAddProductsButton.setOnClickListener(mAddProductsListener);
        mCreateRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent recipeEditorIntent = new Intent(getActivity(), RecipeChangeActivity.class);
                getActivity().startActivity(recipeEditorIntent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mCreateProductButton.setOnClickListener(null);
        mCancelButton.setOnClickListener(null);
        mAddProductsButton.setOnClickListener(null);
    }

    /**
     * Assign to add all selected list items to the current list.
     */
    private View.OnClickListener mAddProductsListener;

    /**
     * Assign to go back to the last fragment.
     */
    private View.OnClickListener mCancelListener;

    /**
     * Assign to call add new product overview.
     */
    private View.OnClickListener mCreateProductListener;

    private class OnAddProductsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            List<SelectableBaseItemListEntry> listEntries = SelectableBaseItemListEntryDataHolder.getInstance().getListEntries();
            IListController mListController = ControllerFactory.getListController();

            for(SelectableBaseItemListEntry listEntry : listEntries){
                // 2 possible solutions for adding to current shoppinglist
                // first would be like add all single items with the controller
                // second would be add all to added products to a list and persist it then to the database --> less db writes when recipes hold same items.
                if(listEntry.isChecked()){
                    BaseItemListEntry baseItemListEntry = listEntry.getItemListEntry();

                    switch (baseItemListEntry.getType()){
                        case PRODUCT_LIST_ENTRY:
                            Product product = (Product)(baseItemListEntry.getEntry().getObject());
                            ListEntry listEntryIntern = mListController.addOrChangeItem(mCurrentShoppingList, product, 1.0f);
                            if(listEntryIntern == null){
                                Log.e(ProductListDialogFragment.class.getName(), "Insertion failed.");
                            }
                            break;
                        case RECIPE_LIST_ENTRY:
                            Recipe recipe = (Recipe) (baseItemListEntry.getEntry().getObject());
                            if(recipe == null){
                                Log.e(ProductListDialogFragment.class.getName(), "recipe is null.");
                                throw new NullPointerException(ProductListDialogFragment.class.getName()
                                        + ": Recipe cannot be found.");
                            }
                            List<Ingredient> ingredients = recipe.getIngredients();
                            for(Ingredient ingredient : ingredients){
                                product = ingredient.mProduct;
                                mListController.addOrChangeItem(mCurrentShoppingList, product, ingredient.mAmount, true);
                            }
                            break;
                        default:
                            throw new IllegalStateException(ProductListDialogFragment.class.toString()
                                    + ". There is a item type that is not handled.");
                    }
                }
            }

            SelectableBaseItemListEntryDataHolder.getInstance().clear();
            // go back to old fragment
            ViewUtils.removeFragment(getActivity(), ProductListDialogFragment.this);
        }
    }

    private class OnCancelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ViewUtils.removeFragment(getActivity(), ProductListDialogFragment.this);
        }
    }

    private class OnCreateProductListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ProductChangeFragment creationFragment =
                    ProductChangeFragment.newCreateInstance(mCurrentShoppingList.getId());
            ViewUtils.addFragment(getActivity(), creationFragment);
            ViewUtils.removeFragment(getActivity(), ProductListDialogFragment.this);
        }
    }
}
