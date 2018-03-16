package hu.am2.letsbake.ui.recipebrowser;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.databinding.ActivityRecipeBrowserBinding;
import hu.am2.letsbake.domain.Result;
import hu.am2.letsbake.ui.recipe.RecipeDetailActivity;

public class RecipeListActivity extends AppCompatActivity implements RecipeBrowserAdapter.RecipeClickListener {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeBrowserViewModel viewModel;

    private static final String TAG = "RecipeListActivity";

    private RecipeBrowserAdapter adapter;

    private ActivityRecipeBrowserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_browser);

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(RecipeBrowserViewModel.class);

        viewModel.getRecipes().observe(this, this::handleRecipes);

        binding.recipeList.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);

        adapter = new RecipeBrowserAdapter(getLayoutInflater(), this);

        binding.recipeList.setAdapter(adapter);
    }

    private void handleRecipes(Result<Recipe> recipes) {
        switch (recipes.status) {
            case LOADING: {
                binding.progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case SUCCESS: {
                binding.progressBar.setVisibility(View.GONE);
                adapter.setRecipes(recipes.data);
                break;
            }
            case ERROR: {
                binding.progressBar.setVisibility(View.GONE);
                adapter.setRecipes(recipes.data);
                Snackbar.make(binding.recipeList, R.string.error, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry, v -> viewModel.retry())
                    .show();
                Log.w(TAG, "handleRecipes: " + recipes.errorMessage);
                break;
            }
        }
    }

    @Override
    public void recipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(Utils.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
