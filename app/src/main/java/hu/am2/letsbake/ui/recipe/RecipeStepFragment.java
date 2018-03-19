package hu.am2.letsbake.ui.recipe;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
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
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.data.remote.model.RecipeStep;
import hu.am2.letsbake.databinding.FragmentRecipeStepBinding;

public class RecipeStepFragment extends Fragment {



    private static final String TAG = "RecipeStepFragment";

    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    private RecipeDetailViewModel viewModel;

    private FragmentRecipeStepBinding binding;

    private SimpleExoPlayer exoPlayer;

    private Dialog fullscreenDialog;

    private boolean isFullScreen = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelProviderFactory).get(RecipeDetailViewModel.class);
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
        viewModel.getPlayerPosition().observe(this, this::handlePlayerPosition);
    }

    private void handlePlayerPosition(Pair<Integer, Long> playerPos) {
        if (playerPos != null) {
            binding.exoPlayer.getPlayer().seekTo(playerPos.first, playerPos.second);
        }
    }

    private void changeFabVisibility(Pair<Integer, Recipe> recipe) {
        if (recipe.first == 0) {
            binding.prevStepFAB.setVisibility(View.GONE);
        } else {
            binding.prevStepFAB.setVisibility(View.VISIBLE);
        }
        if (recipe.first == recipe.second.getSteps().size() - 1) {
            binding.nextStepFAB.setVisibility(View.GONE);
        } else {
            binding.nextStepFAB.setVisibility(View.VISIBLE);
        }
    }

    private void handleRecipeStep(Pair<Integer, Recipe> recipe) {
        RecipeStep recipeStep = recipe.second.getSteps().get(recipe.first);
        binding.recipeStep.setText(recipeStep.getShortDescription());
        releaseExoPlayer();
        if (recipeStep.getVideoURL() == null || recipeStep.getVideoURL().isEmpty() && fullscreenDialog != null) {
            fullscreenDialog.dismiss();
        }
        initExoPlayer(recipeStep.getVideoURL());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.step, recipe.first + 1));
        changeFabVisibility(recipe);
    }

    private void initExoPlayer(String mediaUrl) {

        if (exoPlayer == null && mediaUrl != null && !mediaUrl.isEmpty()) {
            binding.exoPlayer.setVisibility(View.VISIBLE);
            binding.placeHolderImage.setVisibility(View.GONE);
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
        } else {
            binding.exoPlayer.setVisibility(View.GONE);
            binding.placeHolderImage.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipe().removeObservers(this);
        viewModel.getPlayerPosition().removeObservers(this);
        if (exoPlayer != null) {
            int windowIndex = exoPlayer.getCurrentWindowIndex();
            long position = Math.max(0, exoPlayer.getContentPosition());
            viewModel.setPlayerPosition(windowIndex, position);
        }
        if (fullscreenDialog != null) {
            fullscreenDialog.dismiss();
        }
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

        isFullScreen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        binding.exoPlayer.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.cupcake_place_holder));

        //exo player full screen solution based of off: https://github.com/GeoffLedak/ExoplayerFullscreen
        if (isFullScreen && !viewModel.isTwoPane()) {
            ((ViewGroup) binding.exoPlayer.getParent()).removeView(binding.exoPlayer);
            setupFullScreen();
            fullscreenDialog.addContentView(binding.exoPlayer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
            fullscreenDialog.show();
        }

        return binding.getRoot();
    }

    private void setupFullScreen() {
        fullscreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                getActivity().onBackPressed();
            }
        };
    }
}
