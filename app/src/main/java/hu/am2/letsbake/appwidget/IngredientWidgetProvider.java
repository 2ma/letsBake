package hu.am2.letsbake.appwidget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import java.io.File;

import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.ui.recipe.RecipeDetailActivity;
import hu.am2.letsbake.ui.widgetconfiguration.WidgetConfigurationActivity;

public class IngredientWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int id : appWidgetIds) {
            updateAppWidget(context, id, appWidgetManager);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void updateAppWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list_layout);

        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(appWidgetId), Context.MODE_PRIVATE);

        String recipeName = sharedPreferences.getString(Utils.PREFERENCE_RECIPE_TITLE, "");

        //recipe name with click to launch recipe detail
        int id = sharedPreferences.getInt(Utils.PREFERENCE_RECIPE_ID, -1);
        remoteViews.setTextViewText(R.id.recipeName, recipeName);
        Intent recipeDetail = new Intent(context, RecipeDetailActivity.class);
        recipeDetail.putExtra(Utils.EXTRA_RECIPE_ID, id);
        recipeDetail.setData(Uri.parse(recipeDetail.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent rDetail = PendingIntent.getActivity(context, appWidgetId, recipeDetail, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.recipeName, rDetail);

        //click to launch recipe configuration
        Intent recipeConfig = new Intent(context, WidgetConfigurationActivity.class);
        recipeConfig.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        recipeConfig.setData(Uri.parse(recipeConfig.toUri(Intent.URI_INTENT_SCHEME)));
        recipeConfig.setAction(Utils.ACTION_UPDATE_RECIPE);
        PendingIntent configClick = PendingIntent.getActivity(context, appWidgetId, recipeConfig, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.newRecipe, configClick);

        Intent widgetService = new Intent(context, IngredientWidgetService.class);
        widgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetService.setData(Uri.parse(widgetService.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.ingredientList, widgetService);
        remoteViews.setEmptyView(R.id.ingredientList, R.id.emptyView);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.ingredientList);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        final String fileDir = context.getFilesDir().getParent() + "/shared_prefs/";

        for (int appWidgetId : appWidgetIds) {
            final String appWidgetIdString = String.valueOf(appWidgetId);
            context.getSharedPreferences(appWidgetIdString, Context.MODE_PRIVATE).edit().clear().apply();
            final String filePath = fileDir + appWidgetIdString + ".xml";
            final File file = new File(filePath);
            file.delete();

        }
    }
}
