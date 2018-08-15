package dekar.bakerapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import dekar.bakerapp.IdlingResource.SimpleIdlingResource;
import dekar.bakerapp.R;
import dekar.bakerapp.SimpleIdlingResource;
import dekar.bakerapp.adapters.RecipeAdapter;
import dekar.bakerapp.models.Recipe;
import dekar.bakerapp.retrofit.IRecipe;
import dekar.bakerapp.retrofit.RetrofitBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dekar.bakerapp.views.RecipeActivity.ALL_RECIPES;



public class RecipeFragment extends Fragment {



    public RecipeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView;

        View rootView = inflater.inflate(R.layout.recipe_fragment_body_part, container, false);

        recyclerView = rootView.findViewById(R.id.recipe_recycler);
        final RecipeAdapter recipesAdapter = new RecipeAdapter((RecipeActivity)getActivity());
        recyclerView.setAdapter(recipesAdapter);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.recipeColumns));
        recyclerView.setLayoutManager(mLayoutManager);

        IRecipe iRecipe = RetrofitBuilder.Retrieve();
        Call<ArrayList<Recipe>> recipe = iRecipe.getRecipe();

        final SimpleIdlingResource idlingResource = (SimpleIdlingResource)((RecipeActivity)getActivity()).getIdlingResource();

        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        recipe.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                ArrayList<Recipe> recipes = response.body();

                Bundle recipesBundle = new Bundle();
                recipesBundle.putParcelableArrayList(ALL_RECIPES, recipes);
                recipesAdapter.setRecipeData(recipes,getContext());

                recipesAdapter.setRecipeData(recipes,getContext());
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }


            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.v("http fail: ", t.getMessage());
            }
        });

        return rootView;
    }


}
