package org.noorganization.instalist.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.noorganization.instalist.GlobalApplication;
import org.noorganization.instalist.R;
import org.noorganization.instalist.model.ListEntry;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.view.listadapter.ShoppingListAdapter;;

import java.util.ArrayList;
import java.util.List;

/**
 * MainShoppingListView handles the display of an selected shoppinglist, so that the corresponding
 * items of this list are shown to the user.
 *
 * Is dependant on the selected list.
 * @author TS
 */
public class MainShoppingListView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_shopping_list_view);



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_shopping_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private String mCurrentListName;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }


        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();


            ListView shoppingListView;
            ShoppingListAdapter shoppingListAdapter;

            shoppingListView = (ListView) getActivity().findViewById(R.id.fragment_shopping_list);

            GlobalApplication globalApplication = GlobalApplication.getInstance();
            List<ShoppingList> shoppingLists    = globalApplication.getShoppingListNames();

            globalApplication.setCurrentShoppingListName(shoppingLists.get(0).mName);
            List<ListEntry> listOfEntries = globalApplication.getListEntries(globalApplication.getCurrentShoppingListName());

/*            for(int i = 0; i < 50; ++ i) {
                ListEntry listEntry = new ListEntry();
                listEntry.mProduct = new Product();
                listEntry.mProduct.mName = "Sugar " + String.valueOf(i);
                listOfEntries.add(listEntry);
            }
*/
            shoppingListAdapter = new ShoppingListAdapter(getActivity(), listOfEntries);
            shoppingListView.setAdapter(shoppingListAdapter);
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            return  inflater.inflate(R.layout.fragment_main_shopping_list_view, container, false);
        }

    }
}
