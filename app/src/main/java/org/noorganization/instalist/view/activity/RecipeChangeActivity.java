package org.noorganization.instalist.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.noorganization.instalist.R;
import org.noorganization.instalist.view.fragment.RecipeEditorFragment;
import org.noorganization.instalist.view.utils.ViewUtils;

/**
 * Activity that holds the fragments related to recipe creation.
 * Created by TS on 28.04.2015.
 */
public class RecipeChangeActivity extends AppCompatActivity {
    public static final String ARGS_RECIPE_ID = "recipe_id";


    @Override
    public void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);

        setContentView(R.layout.activity_clean_w_actionbar);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        if (_savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent.getExtras() == null || intent.getExtras().isEmpty()) {
                ViewUtils.addFragment(this, RecipeEditorFragment.newCreationInstance());
            } else {
                String recipeId = intent.getExtras().getString(ARGS_RECIPE_ID);
                ViewUtils.addFragment(this, RecipeEditorFragment.newUpdateInstance(recipeId));
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
   }


    @Override
    public void onPause() {
        super.onPause();
    }
}

