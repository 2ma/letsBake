package hu.am2.letsbake.ui.widgetconfiguration;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import hu.am2.letsbake.Utils;
import hu.am2.letsbake.appwidget.IngredientWidgetProvider;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.ui.recipebrowser.RecipeBrowserActivity;

public class WidgetConfigurationActivity extends RecipeBrowserActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean update = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        //if it's not an update to an existing widget, set the result to cancelled
        if (!Utils.ACTION_UPDATE_RECIPE.equals(intent.getAction())) {
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_CANCELED, resultIntent);
            update = false;
        }
    }

    @Override
    public void recipeClick(Recipe recipe) {
        SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(appWidgetId), MODE_PRIVATE).edit();
        String ingredients = Utils.getJsonIngredients(recipe.getIngredients());
        editor.putInt(Utils.PREFERENCE_RECIPE_ID, recipe.getId());
        editor.putString(Utils.PREFERENCE_RECIPE_TITLE, recipe.getName());
        editor.putString(Utils.PREFERENCE_RECIPE_INGREDIENTS, ingredients);
        editor.apply();

        IngredientWidgetProvider.updateAppWidget(this, appWidgetId, AppWidgetManager.getInstance(this));

        if (!update) {
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(Activity.RESULT_OK, resultIntent);
        }
        finish();
    }
}
