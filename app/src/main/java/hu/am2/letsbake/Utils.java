package hu.am2.letsbake;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import hu.am2.letsbake.data.remote.model.RecipeIngredient;

public class Utils {
    public static final String EXTRA_RECIPE_ID = "hu.am2.letsbake.extra.RECIPE_ID";
    public static final String EXTRA_STEP_POSITION = "hu.am2.letsbake.extra.STEP_POSITION";
    public static final String PREFERENCE_RECIPE_ID = "hu.am2.letsbake.preference.RECIPE_ID";
    public static final String PREFERENCE_RECIPE_TITLE = "hu.am2.letsbake.preference.RECIPE_NAME";
    public static final String ACTION_UPDATE_RECIPE = "hu.am2.letsbake.action.UPDATE_RECIPE";
    public static final String PREFERENCE_RECIPE_INGREDIENTS = "hu.am2.letsbake.preference.RECIPE_INGREDIENTS";

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getJsonIngredients(List<RecipeIngredient> ingredients) {
        Type listOfRecipeIngredient = new TypeToken<List<RecipeIngredient>>() {
        }.getType();
        return new Gson().toJson(ingredients, listOfRecipeIngredient);
    }

    public static List<RecipeIngredient> getIngredientsFromJson(String jsonIngredients) {
        Type listOfRecipeIngredient = new TypeToken<List<RecipeIngredient>>() {
        }.getType();
        return new Gson().fromJson(jsonIngredients, listOfRecipeIngredient);
    }
}
