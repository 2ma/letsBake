package hu.am2.letsbake.ui.recipebrowser;


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

public class RecipeBrowserAdapter extends RecyclerView.Adapter<RecipeBrowserAdapter.RecipeViewHolder> {

    private List<Recipe> recipes = Collections.emptyList();
    private final LayoutInflater layoutInflater;
    private final RecipeClickListener listener;

    public interface RecipeClickListener {
        void recipeClick(Recipe recipe);
    }

    public RecipeBrowserAdapter(LayoutInflater layoutInflater, RecipeClickListener listener) {
        this.layoutInflater = layoutInflater;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.browser_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bindRecipe(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView recipeName, recipeServings;
        private final ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeServings = itemView.findViewById(R.id.recipeServings);
        }

        @Override
        public void onClick(View v) {
            listener.recipeClick(recipes.get(getAdapterPosition()));
        }

        void bindRecipe(Recipe recipe) {
            recipeName.setText(recipe.getName());
            recipeServings.setText(recipeServings.getContext().getResources().getQuantityString(R.plurals.servings, recipe.getServings(), recipe
                .getServings()));
            GlideApp.with(recipeImage)
                .load(recipe.getImage())
                .placeholder(R.drawable.cupcake_place_holder)
                .into(recipeImage);
        }
    }

    public void setRecipes(List<Recipe> recipes) {
        if (recipes == null) {
            this.recipes = Collections.emptyList();
        } else {
            this.recipes = recipes;
        }
        notifyDataSetChanged();
    }
}
