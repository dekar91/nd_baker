
package dekar.bakerapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dekar.bakerapp.R;
import dekar.bakerapp.adapters.RecipeDetailAdapter;
import dekar.bakerapp.models.Ingredient;
import dekar.bakerapp.widget.UpdateBakingService;

import static dekar.bakerapp.views.RecipeActivity.SELECTED_RECIPES;


public class RecipeDetailFragment extends Fragment {

    ArrayList<dekar.bakerapp.models.Recipe> recipe;
    String recipeName;

    public RecipeDetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView;
        TextView ingredientsView;

        recipe = new ArrayList<>();


        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelableArrayList(SELECTED_RECIPES);

        } else {
            recipe = getArguments().getParcelableArrayList(SELECTED_RECIPES);
        }

        List<Ingredient> ingredients = recipe.get(0).getIngredients();
        recipeName = recipe.get(0).getName();

        View rootView = inflater.inflate(R.layout.recipe_detail_fragment_body_part, container, false);
        ingredientsView = rootView.findViewById(R.id.recipe_detail_text);

        ArrayList<String> recipeIngredientsForWidgets = new ArrayList<>();


        for (Ingredient ingredient : ingredients) {
            ingredientsView.append("\u2022 " + ingredient.getIngredient() + "\n");
            ingredientsView.append("\t\t\t Quantity: " + ingredient.getQuantity().toString() + "\n");
            ingredientsView.append("\t\t\t Measure: " + ingredient.getMeasure() + "\n\n");

            recipeIngredientsForWidgets.add(ingredient.getIngredient() + "\n" +
                    "Quantity: " + ingredient.getQuantity().toString() + "\n" +
                    "Measure: " + ingredient.getMeasure() + "\n");

        }

        recyclerView = rootView.findViewById(R.id.recipe_detail_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecipeDetailAdapter mRecipeDetailAdapter = new RecipeDetailAdapter((RecipeDetailActivity) getActivity());
        recyclerView.setAdapter(mRecipeDetailAdapter);
        mRecipeDetailAdapter.setMasterRecipeData(recipe, getContext());

        //update widget
        UpdateBakingService.startBakingService(getContext(), recipeIngredientsForWidgets);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putParcelableArrayList(SELECTED_RECIPES, recipe);
        currentState.putString("Title", recipeName);
    }


}


