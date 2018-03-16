package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.databinding.FragmentRecipeListBinding;

public class RecipeDetailListFragment extends Fragment implements RecipeListAdapter.RecipeListClickListener {
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;
    private RecipeDetailViewModel viewModel;

    private FragmentRecipeListBinding binding;

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
        viewModel.getRecipe().observe(this, this::handleRecipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipe().removeObservers(this);
    }

    private void handleRecipe(Recipe recipe) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(recipe.getName());
        adapter.setRecipe(recipe);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);

        binding.recyclerList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeListAdapter(getLayoutInflater(), this);
        binding.recyclerList.setAdapter(adapter);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return binding.getRoot();
    }

    @Override
    public void onRecipeClick(int step) {
        viewModel.setRecipeStep(step);
    }
}
