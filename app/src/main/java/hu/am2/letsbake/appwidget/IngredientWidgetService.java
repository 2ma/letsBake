package hu.am2.letsbake.appwidget;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Collections;
import java.util.List;

import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.data.remote.model.RecipeIngredient;

public class IngredientWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsRemoteViewFactory(intent, this);
    }

    class IngredientsRemoteViewFactory implements RemoteViewsFactory {

        private final SharedPreferences sharedPreferences;
        private List<RecipeIngredient> ingredients = Collections.emptyList();
        private final int appWidgetId;

        public IngredientsRemoteViewFactory(Intent intent, Context context) {
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            this.sharedPreferences = context.getSharedPreferences(String.valueOf(appWidgetId), MODE_PRIVATE);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            String jsonIngredients = sharedPreferences.getString(Utils.PREFERENCE_RECIPE_INGREDIENTS, "");
            ingredients = Utils.getIngredientsFromJson(jsonIngredients);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients == null ? 0 : ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RecipeIngredient ingredient = ingredients.get(position);

            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.ingredient_list_item);
            remoteView.setTextViewText(R.id.ingredient, ingredient.getIngredient());
            remoteView.setTextColor(R.id.ingredient, Color.WHITE);
            remoteView.setTextViewText(R.id.quantity, String.valueOf(ingredient.getQuantity()));
            remoteView.setTextColor(R.id.quantity, Color.WHITE);
            remoteView.setTextViewText(R.id.measure, ingredient.getMeasure());
            remoteView.setTextColor(R.id.measure, Color.WHITE);

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {

            return new RemoteViews(getPackageName(), R.layout.widget_loading);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
