
package dekar.bakerapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import dekar.bakerapp.R;
import dekar.bakerapp.SimpleIdlingResource;
import dekar.bakerapp.adapters.RecipeAdapter;
import dekar.bakerapp.models.Recipe;

import java.util.ArrayList;


public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener{

    static String ALL_RECIPES="All_Recipes";
    static String SELECTED_RECIPES="Selected_Recipes";
    static String SELECTED_STEPS="Selected_Steps";
    static String SELECTED_INDEX="Selected_Index";

    @Nullable
    private IdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_recipe);

       Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
       setSupportActionBar(myToolbar);
       getSupportActionBar().setHomeButtonEnabled(false);
       getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       getSupportActionBar().setTitle(R.string.app_name);

        getIdlingResource();
    }

    @Override
    public void onListItemClick(Recipe selectedItemIndex) {

        Bundle selectedRecipeBundle = new Bundle();
        ArrayList<Recipe> selectedRecipe = new ArrayList<>();
        selectedRecipe.add(selectedItemIndex);
        selectedRecipeBundle.putParcelableArrayList(SELECTED_RECIPES,selectedRecipe);

        final Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtras(selectedRecipeBundle);
        startActivity(intent);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
    }


}
