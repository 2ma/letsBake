package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.Utils;
import hu.am2.letsbake.databinding.ActivityRecipeStepBinding;

public class RecipeStepActivity extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeDetailViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        ActivityRecipeStepBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_step);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(RecipeDetailViewModel.class);

        Intent intent = getIntent();

        int recipeId = intent.getIntExtra(Utils.EXTRA_RECIPE_ID, -1);
        int step = intent.getIntExtra(Utils.EXTRA_STEP_POSITION, -1);

        if (recipeId != -1 && step != -1) {
            viewModel.setRecipeStepAndId(recipeId, step);
        } else {
            Toast.makeText(this, R.string.recipe_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setupActivityResult();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setupActivityResult();
        super.onBackPressed();
    }

    //send back active recipe step, so the recipe step list can be updated accordingly
    private void setupActivityResult() {
        int step = viewModel.getRecipeStepNumber();
        if (step != -1) {
            final Intent intent = new Intent();
            intent.putExtra(Utils.EXTRA_STEP_POSITION, step);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
    }
}
