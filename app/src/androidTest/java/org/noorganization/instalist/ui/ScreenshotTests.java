package org.noorganization.instalist.ui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noorganization.instalist.R;
import org.noorganization.instalist.model.Category;
import org.noorganization.instalist.model.Product;
import org.noorganization.instalist.model.Recipe;
import org.noorganization.instalist.model.ShoppingList;
import org.noorganization.instalist.model.Unit;
import org.noorganization.instalist.presenter.IListController;
import org.noorganization.instalist.presenter.IProductController;
import org.noorganization.instalist.presenter.IRecipeController;
import org.noorganization.instalist.presenter.IUnitController;
import org.noorganization.instalist.presenter.implementation.ControllerFactory;
import org.noorganization.instalist.view.activity.MainShoppingListView;

import java.util.ArrayList;
import java.util.List;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Enables the function to take screenshots of the app with fastlane tool screengrab
 * Created by lunero on 16.05.16.
 */
@RunWith(AndroidJUnit4.class)
public class ScreenshotTests {
    private Context mMockContext;

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainShoppingListView> activityRule  = new ActivityTestRule<>(MainShoppingListView.class);

    @Before
    public void setUp(){
        mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");

        clearData();
        Category privateCategory = ControllerFactory.getCategoryController(mMockContext).createCategory("private");
        Category businessCategory = ControllerFactory.getCategoryController(mMockContext).createCategory("business");

        IListController listController = ControllerFactory.getListController(mMockContext);
        IUnitController unitController = ControllerFactory.getUnitController(mMockContext);
        IProductController productController = ControllerFactory.getProductController(mMockContext);
        IRecipeController recipeController = ControllerFactory.getRecipeController(mMockContext);

        // create pseudo entries
        listController.addList("Mall");
        listController.addList("Tool Workshop");
        ShoppingList tomsBirthdayList = listController.addList("Tom's Birthday", privateCategory);
        listController.addList("Big Business Party", businessCategory);

        Unit gUnit =  unitController.createUnit("g");
        Unit lUnit =  unitController.createUnit("l");
        Unit parcel = unitController.createUnit("parcel");

        List<Product> productList = new ArrayList<>();
        Product sugar       = productController.createProduct("Sugar",gUnit,50.0f,1.0f);
        Product flour       = productController.createProduct("Flour",gUnit,500.0f,1.0f);
        Product milk        = productController.createProduct("Milk",lUnit,200.0f,1.0f);
        Product saleratus   = productController.createProduct("Saleratus", parcel, 1.0f, 1.0f);
        productList.add(sugar);
        productList.add(flour);
        productList.add(milk);
        productList.add(productController.createProduct("Balloons",null,40.0f,1.0f));
        productList.add(productController.createProduct("Candles",null,12.0f,1.0f));
        productList.add(productController.createProduct("Garland",null,5.0f,1.0f));
        productList.add(productController.createProduct("Confetti",null,10.0f,1.0f));
        productList.add(productController.createProduct("Chocolate",gUnit,500.0f,1.0f));
/*
        for(Product product : productList){
            listController.addOrChangeItem(tomsBirthdayList,product, product.mDefaultAmount);
        }

        Recipe cake= recipeController.createRecipe("Cake");
        recipeController.addOrChangeIngredient(cake, sugar, 50.0f);
        recipeController.addOrChangeIngredient(cake, flour, 300.0f);
        recipeController.addOrChangeIngredient(cake, milk, 100.0f);
        recipeController.addOrChangeIngredient(cake, saleratus, 1.0f);
*/
    }

    @After
    public void clear(){
       // clearData();
    }


    private void clearData(){

        IListController listController = ControllerFactory.getListController(mMockContext);
        IUnitController unitController = ControllerFactory.getUnitController(mMockContext);
        IProductController productController = ControllerFactory.getProductController(mMockContext);
        IRecipeController recipeController = ControllerFactory.getRecipeController(mMockContext);

        List<Category> categoryList = ControllerFactory.getCategoryController(mMockContext).getAllCategories();
        List<ShoppingList> shoppingLists = listController.getAllLists();
        List<Unit> units = unitController.listAll(Unit.COLUMN.NAME, true);
        List<Recipe> recipes = recipeController.listAll();
        List<Product> products = productController.listAll();

        for(Product product : products){
            productController.removeProduct(product, true);
        }

        for(Recipe recipe : recipes){
            recipeController.removeRecipe(recipe);
        }

        for(Unit unit : units){
            unitController.deleteUnit(unit, IUnitController.MODE_DELETE_REFERENCES);
        }
        // clean entries
        for(Category category : categoryList){
            ControllerFactory.getCategoryController(mMockContext).removeCategory(category);
        }
        for(ShoppingList shoppingList : shoppingLists){
            listController.removeList(shoppingList);
        }
    }

    @Test
    public void testTakeScreenshot(){
        Screengrab.screenshot("mainList");
       // onView(withContentDescription(R.string.nav_drawer_open)).perform(click());
        Screengrab.screenshot("drawerView");
        //onView(withContentDescription(R.string.nav_drawer_close)).perform(click());

        //onView(withId(R.id.add_item_main_list_view)).perform(click());
        Screengrab.screenshot("productSelection");

    }

}
