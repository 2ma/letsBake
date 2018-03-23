package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.SimpleIdlingResource;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.databinding.ActivityRecipeDetailBinding;

public class RecipeDetailActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeDetailViewModel viewModel;

    private boolean isTwoPane;

    @Nullable
    private SimpleIdlingResource simpleIdlingResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ActivityRecipeDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_detail);

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(RecipeDetailViewModel.class);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        isTwoPane = getResources().getBoolean(R.bool.isTablet);

        //setup welcome screen for tablet mode
        if (isTwoPane && getSupportFragmentManager().findFragmentByTag("step") == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, new WelcomeFragment(), "welcome").commit();
        }

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

        viewModel.getRecipeStepActivityTrackerLiveData().observe(this, this::handleRecipeStepActivityTracker);
    }

    private void handleRecipeStepActivityTracker(int step) {
        if (step != -1) {
            if (isTwoPane) {
                viewModel.setStep(step);
                RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.detailContainer, recipeStepFragment, "step").commit();
            } else {
                Intent intent = new Intent(this, RecipeStepActivity.class);
                intent.putExtra(Utils.EXTRA_STEP_POSITION, step);
                intent.putExtra(Utils.EXTRA_RECIPE_ID, viewModel.getRecipeId());
                startActivityForResult(intent, 1337);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1337) {
            int step = data.getIntExtra(Utils.EXTRA_STEP_POSITION, 0);
            //set fragment tracker to step, so it can scroll to the selected step and mark it as active
            viewModel.setRecipeStepDetailListFragmentTracker(step);
            //set the activity tracker to invalid, so it the device is rotated the last recipe step won't be launched
            viewModel.setRecipeStepActivityTracker(-1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int rId = viewModel.getRecipeId();
        if (rId != -1) {
            outState.putInt(Utils.EXTRA_RECIPE_ID, viewModel.getRecipeId());
        }
        super.onSaveInstanceState(outState);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (simpleIdlingResource == null) {
            simpleIdlingResource = new SimpleIdlingResource();
        }
        return simpleIdlingResource;
    }

    @Nullable
    IdlingResource getIdlingResourceForFragment() {
        return simpleIdlingResource;
    }
}
