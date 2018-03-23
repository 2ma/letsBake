package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.SimpleIdlingResource;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.databinding.FragmentRecipeListBinding;
import hu.am2.letsbake.domain.Result;

public class RecipeDetailListFragment extends Fragment implements RecipeListAdapter.RecipeListListener {

    private static final String TAG = "RecipeDetailListFragmen";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    private RecipeDetailViewModel viewModel;
    private FragmentRecipeListBinding binding;

    private SimpleIdlingResource simpleIdlingResource;

    private RecipeListAdapter adapter;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelProviderFactory).get(RecipeDetailViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        simpleIdlingResource = (SimpleIdlingResource) ((RecipeDetailActivity) getActivity()).getIdlingResourceForFragment();
        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }
        viewModel.getRecipeLiveData().observe(this, this::handleRecipe);
        viewModel.getRecipeStepDetailListFragmentTracker().observe(this, this::handleRecipeStep);
    }

    private void handleRecipeStep(Integer step) {
        adapter.setSelectedRecipeStep(step);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipeLiveData().removeObservers(this);
    }

    private void handleRecipe(Result<Recipe> result) {
        switch (result.status) {
            case LOADING: {
                binding.progressBar.setVisibility(View.VISIBLE);
                break;
            }
            case SUCCESS: {
                binding.progressBar.setVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(result.data.getName());
                if (simpleIdlingResource != null) {
                    simpleIdlingResource.setIdleState(true);
                }
                adapter.setRecipe(result.data);
                break;
            }
            case ERROR: {
                binding.progressBar.setVisibility(View.GONE);
                if (simpleIdlingResource != null) {
                    simpleIdlingResource.setIdleState(true);
                }
                Snackbar.make(binding.recipeDetailList, R.string.error, Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "handleRecipe: " + result.errorMessage);
                break;
            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);

        boolean isTwoPane = getResources().getBoolean(R.bool.isTablet);

        binding.recipeDetailList.setLayoutManager(new GridLayoutManager(getContext(), isTwoPane ? 1 : numberOfGrid()));
        adapter = new RecipeListAdapter(getLayoutInflater(), this);
        binding.recipeDetailList.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onRecipeClick(int step) {
        viewModel.setRecipeStepActivityTracker(step);
    }

    @Override
    public void scrollToPosition(int position) {
        binding.recipeDetailList.scrollToPosition(position);
    }

    private int numberOfGrid() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int minWidth = 600;

        return displayMetrics.widthPixels / minWidth;
    }
}
