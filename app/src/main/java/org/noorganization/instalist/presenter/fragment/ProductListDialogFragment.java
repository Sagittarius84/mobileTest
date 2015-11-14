package org.noorganization.instalist.presenter.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import org.noorganization.instalist.R;
import org.noorganization.instalist.controller.IListController;
import org.noorganization.instalist.controller.event.ProductChangedMessage;
import org.noorganization.instalist.controller.event.RecipeChangedMessage;
import org.noorganization.instalist.controller.implementation.ControllerFactory;
import org.noorganization.instalist.model.Ingredient;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.presenter.activity.RecipeChangeActivity;
import org.noorganization.instalist.presenter.event.ProductSelectMessage;
import org.noorganization.instalist.presenter.event.ToolbarChangeMessage;
import org.noorganization.instalist.presenter.interfaces.IBaseActivity;
import org.noorganization.instalist.presenter.listadapter.SelectableItemListAdapter;
import org.noorganization.instalist.presenter.modelwrappers.IBaseListEntry;
import org.noorganization.instalist.presenter.modelwrappers.ProductListEntry;
import org.noorganization.instalist.presenter.modelwrappers.RecipeListEntry;
import org.noorganization.instalist.presenter.sorting.selectableList.AlphabeticalIBaseListEntryComparator;
import org.noorganization.instalist.presenter.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by TS on 10.05.2015.
 * Responsible to show a dialog with a list of selectable products to add them to an existing shopping
 * list.
 */
public class ProductListDialogFragment extends BaseFragment {

    private static final String LOG_TAG = ProductListDialogFragment.class.getName();

    public static final String FILTER_BY_PRODUCT = "0";
    public static final String FILTER_BY_RECIPE = "1";
    public static final String FILTER_SHOW_ALL = "2";
    public static final String SAVED_ITEM_LIST = "SAVED_ITEM_LIST";

    private Button mCreateProductButton;
    private Button mAddProductsButton;
    private Button mCreateRecipeButton;
    private ListView mMixedListView;

    private static final String BUNDLE_KEY_LIST_ID = "listId";
    private static final String BK_COMPABILITY = "comp";
    private static final String BK_ALLOW_RECIPE_CREATION = "recipeCreation";

    // create the abstract selectable list entries to show mixed entries
    private ArrayList<IBaseListEntry> mSelectableBaseItemListEntries = new ArrayList<>();
    private SelectableItemListAdapter mListAdapter;

    private ListAddModeCompability mCompatibility;

    private IBaseActivity mBaseActivityInterface;
    private Context mContext;


    /**
     * Creates an instance with coupling to ShoppingList.
     *
     * @param _listId                The list's id to couple. If not existing, an instance without coupling will be
     *                               returned.
     * @param _recipeCreationEnabled Whether recipe creation should be allowed or not.
     * @return The new instance.
     */
    public static ProductListDialogFragment newInstance(long _listId, boolean _recipeCreationEnabled) {
        ProductListDialogFragment instance = newInstance(_listId);
        instance.getArguments().putBoolean(BK_ALLOW_RECIPE_CREATION, _recipeCreationEnabled);
        return instance;
    }

    /**
     * Creates an instance without coupling to ShoppingList.
     *
     * @param _recipeCreationEnabled Whether recipe creation should be allowed or not.
     * @return The new instance.
     */
    public static ProductListDialogFragment newInstance(boolean _recipeCreationEnabled) {
        ProductListDialogFragment instance = newInstance();
        instance.getArguments().putBoolean(BK_ALLOW_RECIPE_CREATION, _recipeCreationEnabled);
        return instance;
    }

    /**
     * Creates an instance of an ProductListDialogFragment.
     *
     * @param _ListId the id of the list where the products should be added.
     * @return the new instance of this fragment.
     */
    public static ProductListDialogFragment newInstance(long _ListId) {
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(BK_COMPABILITY, true);
        args.putLong(BUNDLE_KEY_LIST_ID, _ListId);
        args.putBoolean(BK_ALLOW_RECIPE_CREATION, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProductListDialogFragment newInstance() {
        ProductListDialogFragment fragment = new ProductListDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(BK_COMPABILITY, false);
        args.putBoolean(BK_ALLOW_RECIPE_CREATION, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onAttachToContext(Context _Context) {
        mContext = _Context;
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

        if (bundle.getBoolean(BK_COMPABILITY)) {
            mCompatibility = new ListAddModeCompability(bundle.getString(BUNDLE_KEY_LIST_ID));
        }


        if (_savedInstanceState == null || !_savedInstanceState.containsKey(SAVED_ITEM_LIST)) {
            List<Product> productList = ControllerFactory.getProductController(mContext).listAll();
            List<Recipe> recipeList = ControllerFactory.getRecipeController(mContext).listAll();
            //List<ListEntry> listEntries = mCurrentShoppingList.getEntries();

            // remove all inserted list entries
            // TODO add a method for hiding products.
        /*for(ListEntry listEntry : listEntries){
            productList.remove(listEntry.mProduct);
        }*/

            for (Product product : productList) {
                mSelectableBaseItemListEntries.add(new ProductListEntry(product));
            }

            for (Recipe recipe : recipeList) {
                mSelectableBaseItemListEntries.add(new RecipeListEntry(recipe));
            }
        } else {
            mSelectableBaseItemListEntries = _savedInstanceState.getParcelableArrayList(SAVED_ITEM_LIST);
        }

        mAddProductsListener = new OnAddProductsListener();
        mCreateProductListener = new OnCreateProductListener();

        if (mCompatibility != null) {
            EventBus.getDefault().register(mCompatibility);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(Bundle _SavedIndstance) {
        super.onActivityCreated(_SavedIndstance);

    }

    @Override
    public View onCreateView(LayoutInflater _Inflater, ViewGroup _Container, Bundle _SavedInstanceState) {
        super.onCreateView(_Inflater, _Container, _SavedInstanceState);

        View view = _Inflater.inflate(R.layout.fragment_product_list_dialog, _Container, false);

        mListAdapter = new SelectableItemListAdapter(getActivity(), R.layout.list_selectable_product, mSelectableBaseItemListEntries);

        mCreateProductButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_new_product);
        mAddProductsButton = (Button) view.findViewById(R.id.fragment_product_list_dialog_add_products_to_list);
        mCreateRecipeButton = (Button) view.findViewById(R.id.testRecipeButton);

        mMixedListView = (ListView) view.findViewById(R.id.fragment_product_list_dialog_product_list_view);

        mListAdapter.getFilter().filter(FILTER_SHOW_ALL);
        mMixedListView.setAdapter(mListAdapter);

        if (!getArguments().getBoolean(BK_ALLOW_RECIPE_CREATION)) {
            mCreateRecipeButton.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu _Menu, MenuInflater _Inflater) {
        _Menu.clear();
        _Inflater.inflate(R.menu.menu_product_list_dialog, _Menu);

        // adds search ability to the toolbar
        MenuItem searchItem = _Menu.findItem(R.id.menu_product_list_dialog_search);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView == null) {
            throw new NullPointerException("Either the Search action in toolbar is not assigned"
                    + " as item or there is no SearchView to actionViewClass in menu assigned.");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mListAdapter.getFilter().filter(s);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mListAdapter.getFilter().filter("");
                return false;
            }
        });
        /*searchView.setSearchableInfo(MainShoppingListView.this.getComponentName());*/
        super.onCreateOptionsMenu(_Menu, _Inflater);
    }

    /**
     * Handles events for recipe changes.
     *
     * @param _message
     */
    public void onEventMainThread(RecipeChangedMessage _message) {
        RecipeListEntry recipeEntry = new RecipeListEntry(_message.mRecipe);
        switch (_message.mChange) {
            case CREATED:
                mListAdapter.addItem(recipeEntry);
                break;
            case CHANGED:
                mListAdapter.changeItem(recipeEntry);
                break;
            case DELETED:
                mListAdapter.removeItem(recipeEntry);
                break;
        }
    }

    /**
     * Handles events for product changes.
     *
     * @param _message
     */
    public void onEventMainThread(ProductChangedMessage _message) {
        ProductListEntry productEntry = new ProductListEntry(_message.mProduct);
        switch (_message.mChange) {
            case CREATED:
                mListAdapter.addItem(productEntry);
                break;
            case CHANGED:
                mListAdapter.changeItem(productEntry);
                break;
            case DELETED:
                mListAdapter.removeItem(productEntry);
                break;
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu _Menu) {
        // TODO: try to remove this, it seems very very hacky
        // found at: http://stackoverflow.com/questions/10445760/how-to-change-the-default-icon-on-the-searchview-to-be-use-in-the-action-bar-on
        if (_Menu == null) {
            return;
        }
        MenuItem searchMenuItem = _Menu.findItem(R.id.menu_product_list_dialog_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchMenuItem.setIcon(R.drawable.ic_search_white_36dp);


        int searchTextViewId = android.support.v7.appcompat.R.id.search_src_text;
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(searchTextViewId);
        searchTextView.setHintTextColor(getResources().getColor(R.color.white));
        searchTextView.setTextColor(getResources().getColor(android.R.color.white));
        searchTextView.setTextSize(16.0f);


        SpannableStringBuilder ssb = new SpannableStringBuilder("   "); // for the icon
        //ssb.append(hintText);
        Drawable searchIcon = getResources().getDrawable(R.drawable.ic_search_white_36dp);
        int textSize = (int) (searchTextView.getTextSize() * 1.25);
        searchIcon.setBounds(0, 0, textSize, textSize);
        ssb.setSpan(new ImageSpan(searchIcon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchTextView.setHint(ssb);
        super.onPrepareOptionsMenu(_Menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
                mListAdapter.setComparator(new AlphabeticalIBaseListEntryComparator());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().post(new ToolbarChangeMessage(true, mContext.getString(R.string.product_list_dialog_title)));

        mCreateProductButton.setOnClickListener(mCreateProductListener);
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
        mAddProductsButton.setOnClickListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle _OutState) {
        _OutState.putParcelableArrayList(SAVED_ITEM_LIST, mSelectableBaseItemListEntries);
        super.onSaveInstanceState(_OutState);
    }

    @Override
    public void onDestroy() {
        if (mCompatibility != null) {
            EventBus.getDefault().unregister(mCompatibility);
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Assign to add all selected list items to the current list.
     */
    private View.OnClickListener mAddProductsListener;

    /**
     * Assign to call add new product overview.
     */
    private View.OnClickListener mCreateProductListener;

    private class ListAddModeCompability {

        private ShoppingList mCurrentShoppingList;

        public ListAddModeCompability(String _id) {
            mCurrentShoppingList = ControllerFactory.getListController(mContext).getListById(_id);

            if (mCurrentShoppingList == null) {
                throw new IllegalStateException(ProductListDialogFragment.class.toString() +
                        ": Cannot find corresponding ShoppingList for id: " + _id);
            }
        }

        /**
         * EventBus-receiver for translation to listentries.
         *
         * @param _selectedProducts
         */
        public void onEventMainThread(ProductSelectMessage _selectedProducts) {
            IListController mListController = ControllerFactory.getListController(mContext);

            for (Product product : _selectedProducts.mProducts.keySet()) {
                // 2 possible solutions for adding to current shoppinglist
                // first would be like add all single items with the controller
                // second would be add all to added products to a list and persist it then to the database --> less db writes when recipes hold same items.

                ListEntry listEntryIntern = mListController.addOrChangeItem(mCurrentShoppingList,
                        product, _selectedProducts.mProducts.get(product));
                if (listEntryIntern == null) {
                    Log.e(ProductListDialogFragment.class.getName(), "Insertion failed.");
                }
            }
        }


    }

    private class OnAddProductsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Iterator<IBaseListEntry> listEntryIterator = mListAdapter.getCheckedListEntries();
            Map<Product, Float> resultingProducts = new HashMap<>();

            while (listEntryIterator.hasNext()) {
                IBaseListEntry listEntry = listEntryIterator.next();

                // skip this entry when not selected/checked
                if (!listEntry.isChecked()) {
                    continue;
                }

                switch (listEntry.getType()) {
                    case PRODUCT_LIST_ENTRY:
                        Product product = (Product) (listEntry.getItem());
                        float amountToAdd = product.mDefaultAmount;
                        if (resultingProducts.containsKey(product)) {
                            resultingProducts.put(product, resultingProducts.get(product) + amountToAdd);
                        } else {
                            resultingProducts.put(product, product.mDefaultAmount);
                        }
                        break;
                    case RECIPE_LIST_ENTRY:
                        Recipe recipe = (Recipe) (listEntry.getItem());
                        List<Ingredient> ingredients = ControllerFactory.getRecipeController(mContext).getIngredients(recipe.mUUID);
                        for (Ingredient ingredient : ingredients) {
                            if (resultingProducts.containsKey(ingredient.mProduct)) {
                                resultingProducts.put(ingredient.mProduct,
                                        resultingProducts.get(ingredient.mProduct) + ingredient.mAmount);
                            } else {
                                resultingProducts.put(ingredient.mProduct, ingredient.mAmount);
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException(ProductListDialogFragment.class.toString()
                                + ". There is a item type that is not handled.");
                }

            }
            EventBus.getDefault().post(new ProductSelectMessage(resultingProducts));

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
            ProductChangeFragment creationFragment = ProductChangeFragment.newCreateInstance();
            ViewUtils.addFragment(getActivity(), creationFragment);
            ViewUtils.removeFragment(getActivity(), ProductListDialogFragment.this);
        }
    }


}
