package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.databinding.ActivityRecipeDetailBinding;

public class RecipeDetailActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeDetailViewModel viewModel;

    private boolean isTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ActivityRecipeDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail);

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(RecipeDetailViewModel.class);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        isTwoPane = findViewById(R.id.detailContainer) != null;

        int recipeId = intent.getIntExtra(Utils.EXTRA_RECIPE_ID, -1);

        if (recipeId == -1 && savedInstanceState != null) {
            recipeId = savedInstanceState.getInt(Utils.EXTRA_RECIPE_ID);
        }

        if (recipeId != -1) {
            viewModel.setRecipeId(recipeId);
        } else {
            Toast.makeText(this, R.string.recipe_error, Toast.LENGTH_SHORT).show();
            finish();
        }

        viewModel.getRecipeStepNumber().observe(this, this::handleRecipeStep);
    }

    private void handleRecipeStep(int step) {
        if (isTwoPane) {
            viewModel.setTwoPane(true);
            viewModel.setStep(step);
            RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.detailContainer, recipeStepFragment, "step").commit();
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(Utils.EXTRA_STEP_POSITION, step);
            intent.putExtra(Utils.EXTRA_RECIPE_ID, viewModel.getRecipeId());
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Utils.EXTRA_RECIPE_ID, viewModel.getRecipeId());
        super.onSaveInstanceState(outState);
    }
}
