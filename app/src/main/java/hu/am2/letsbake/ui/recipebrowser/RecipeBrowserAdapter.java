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
    private LayoutInflater layoutInflater;
    private RecipeClickListener listener;

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

        private final TextView recipeName;
        private final ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }

        @Override
        public void onClick(View v) {
            listener.recipeClick(recipes.get(getAdapterPosition()));
        }

        void bindRecipe(Recipe recipe) {
            recipeName.setText(recipe.getName());
            GlideApp.with(recipeImage)
                .load(recipe.getImage())
                .placeholder(R.drawable.cupcake_place_holder)
                .into(recipeImage);
        }
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }
}
