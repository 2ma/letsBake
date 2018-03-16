package hu.am2.letsbake.ui.recipebrowser;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.databinding.FragmentRecipeBrowserBinding;

public class RecipeBrowserFragment extends Fragment implements RecipeBrowserAdapter.RecipeClickListener {

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private FragmentRecipeBrowserBinding binding;

    private RecipeBrowserViewModel viewModel;
    private RecipeBrowserAdapter adapter;

    private static final String TAG = "RecipeBrowserFragment";

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelProviderFactory).get(RecipeBrowserViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_browser, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipes().removeObservers(this);
    }

    @Override
    public void recipeClick(Recipe recipe) {
        //TODO pass to viewmodel -> activity
        //viewModel.recipeSelected(recipe);
    }
}
