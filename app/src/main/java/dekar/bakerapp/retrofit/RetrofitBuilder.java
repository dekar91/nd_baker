
package dekar.bakerapp.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dekar.bakerapp.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class RetrofitBuilder {
    static IRecipe iRecipe;

    public static IRecipe Retrieve() {

        Gson gson = new GsonBuilder().create();

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        iRecipe = new Retrofit.Builder()
                .baseUrl(BuildConfig.RECIPE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build().create(IRecipe.class);


        return iRecipe;
    }
}

