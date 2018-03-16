package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import hu.am2.letsbake.R;
import hu.am2.letsbake.data.remote.model.RecipeStep;
import hu.am2.letsbake.databinding.FragmentRecipeStepBinding;

public class RecipeStepFragment extends Fragment {

    public static final String EXTRA_STEP_POSITION = "hu.am2.letsbake.extra.STEP_POSITION";

    private static final String TAG = "RecipeStepFragment";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeDetailViewModel viewModel;

    private FragmentRecipeStepBinding binding;

    private SimpleExoPlayer exoPlayer;

    private int step;

    public static RecipeStepFragment getInstance(int step) {

        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();

        Bundle bundle = new Bundle();

        bundle.putInt(EXTRA_STEP_POSITION, step);

        recipeStepFragment.setArguments(bundle);

        return recipeStepFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelProviderFactory).get(RecipeDetailViewModel.class);

        Bundle bundle = getArguments();

        step = bundle.getInt(EXTRA_STEP_POSITION);

        viewModel.setStep(step);

    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getRecipeStep().observe(this, this::handleRecipeStep);
    }

    private void changeFabVisibility() {
        if (step == 0) {
            binding.prevStepFAB.setVisibility(View.GONE);
        } else {
            binding.prevStepFAB.setVisibility(View.VISIBLE);
        }
        if (step == viewModel.getMaximumSteps()) {
            binding.nextStepFAB.setVisibility(View.GONE);
        } else {
            binding.nextStepFAB.setVisibility(View.VISIBLE);
        }
    }

    private void handleRecipeStep(Pair<Integer, RecipeStep> recipeStep) {
        binding.recipeStep.setText(recipeStep.second.getShortDescription());
        initExoPlayer(recipeStep.second.getVideoURL());
        step = recipeStep.first;
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.step, step + 1));
        changeFabVisibility();
    }

    private void initExoPlayer(String mediaUrl) {

        if (exoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            binding.exoPlayer.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(getContext(), "LetsBake");
            ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getContext(), userAgent));
            factory.setExtractorsFactory(new DefaultExtractorsFactory());

            MediaSource mediaSource = factory.createMediaSource(Uri.parse(mediaUrl));
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipe().removeObservers(this);
        releaseExoPlayer();
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_step, container, false);

        binding.nextStepFAB.setOnClickListener(v -> {
                releaseExoPlayer();
                viewModel.nextClick();
            }
        );

        binding.prevStepFAB.setOnClickListener(v -> {
                releaseExoPlayer();
                viewModel.prevClick();
            }
        );

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeFabVisibility();

        return binding.getRoot();
    }
}
