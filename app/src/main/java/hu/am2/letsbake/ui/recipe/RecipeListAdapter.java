package hu.am2.letsbake.ui.recipe;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.am2.letsbake.GlideApp;
import hu.am2.letsbake.R;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.data.remote.model.RecipeIngredient;
import hu.am2.letsbake.data.remote.model.RecipeStep;

public class RecipeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private RecipeListClickListener listener;
    private List<RecipeStep> steps = Collections.emptyList();
    private List<RecipeIngredient> ingredients = Collections.emptyList();

    private static final int INGREDIENT_TYPE = 0;
    private static final int STEP_TYPE = 1;

    public interface RecipeListClickListener {
        void onRecipeClick(int step);
    }

    public RecipeListAdapter(LayoutInflater inflater, RecipeListClickListener listener) {
        this.inflater = inflater;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == INGREDIENT_TYPE) {
            View view = inflater.inflate(R.layout.ingredient_list_item, parent, false);

            return new IngredientViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.browser_list_item, parent, false);

            return new RecipeStepViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == INGREDIENT_TYPE) {
            ((IngredientViewHolder) holder).bindIngredient(ingredients.get(position));
        } else {
            ((RecipeStepViewHolder) holder).bindStep(steps.get(position - ingredients.size()));
        }
    }

    @Override
    public int getItemCount() {
        return steps.size() + ingredients.size();
    }

    public void setRecipe(Recipe recipe) {
        steps = recipe.getSteps();
        ingredients = recipe.getIngredients();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < ingredients.size()) {
            return INGREDIENT_TYPE;
        } else {
            return STEP_TYPE;
        }
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {

        private TextView ingredientName, quantity, measure;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.ingredient);
            quantity = itemView.findViewById(R.id.quantity);
            measure = itemView.findViewById(R.id.measure);
        }

        public void bindIngredient(RecipeIngredient ingredient) {
            ingredientName.setText(ingredient.getIngredient());
            quantity.setText(String.valueOf(ingredient.getQuantity()));
            measure.setText(ingredient.getMeasure());
        }
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView stepName;
        private ImageView stepThumbnail;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            stepName = itemView.findViewById(R.id.recipeName);
            stepThumbnail = itemView.findViewById(R.id.recipeImage);
        }

        @Override
        public void onClick(View v) {
            listener.onRecipeClick(getAdapterPosition() - ingredients.size());
        }

        public void bindStep(RecipeStep step) {
            stepName.setText(step.getShortDescription());
            GlideApp.with(stepThumbnail)
                .load(step.getThumbnailURL())
                .placeholder(R.drawable.recipe_placeholder)
                .into(stepThumbnail);
        }
    }
}
