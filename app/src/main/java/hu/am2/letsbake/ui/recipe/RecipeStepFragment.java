package hu.am2.letsbake.ui.recipe;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.exoplayer2.util.Util;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import hu.am2.letsbake.CacheDataSourceFactory;
import hu.am2.letsbake.ExoPlayerState;
import hu.am2.letsbake.GlideApp;
import hu.am2.letsbake.R;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.data.remote.model.RecipeStep;
import hu.am2.letsbake.databinding.FragmentRecipeStepBinding;

public class RecipeStepFragment extends Fragment {

    private static final String EXTRA_EXOPLAYER_STATE = "hu.am2.letsbake.extra.EXOPLAYER_STATE";
    @Inject
    ViewModelProvider.Factory viewModelProviderFactory;

    @Inject
    CacheDataSourceFactory cacheDataSource;

    private RecipeDetailViewModel viewModel;

    private FragmentRecipeStepBinding binding;

    private SimpleExoPlayer exoPlayer;

    private Dialog fullscreenDialog;

    private MediaSource videoSource = null;

    private boolean isFullscreen = false;

    private boolean isTwoPane = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelProviderFactory).get(RecipeDetailViewModel.class);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_EXOPLAYER_STATE)) {
            viewModel.setExoPlayerState(savedInstanceState.getParcelable(EXTRA_EXOPLAYER_STATE));
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel.getRecipeStepLiveData().observe(this, this::handleRecipeStep);
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
        int step = recipe.first;
        RecipeStep recipeStep = recipe.second.getSteps().get(step);
        binding.recipeStepDescription.setText(recipeStep.getDescription());
        releaseExoPlayer();
        /*if (recipeStep.getVideoURL() == null || recipeStep.getVideoURL().isEmpty() && fullscreenDialog != null) {
            fullscreenDialog.dismiss();
        }*/

        if (recipeStep.getVideoURL() == null || recipeStep.getVideoURL().isEmpty()) {
            setupPlaceholderImage(recipeStep.getThumbnailURL());
        } else {
            prepareVideoSource(recipeStep.getVideoURL());
            initExoPlayer();
            fullScreenMode();
        }
        if (step == 0) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.preparation));
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.step, step));
        }
        if (!isTwoPane) {
            changeFabVisibility(recipe);
        }
    }

    //exo player full screen solution based of off: https://github.com/GeoffLedak/ExoplayerFullscreen
    private void fullScreenMode() {
        if (isFullscreen && exoPlayer != null) {
            ((ViewGroup) binding.exoPlayer.getParent()).removeView(binding.exoPlayer);
            fullscreenDialog.addContentView(binding.exoPlayer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));
            fullscreenDialog.show();
        }
    }

    private void setupPlaceholderImage(String thumbnailUrl) {
        binding.exoPlayer.setVisibility(View.GONE);
        binding.placeHolderImage.setVisibility(View.VISIBLE);
        GlideApp.with(binding.placeHolderImage)
            .load(thumbnailUrl)
            .placeholder(R.drawable.cupcake_place_holder)
            .into(binding.placeHolderImage);
    }

    private void prepareVideoSource(String videoUrl) {
        String userAgent = Util.getUserAgent(getContext(), "LetsBake");
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(cacheDataSource);
        factory.setExtractorsFactory(new DefaultExtractorsFactory());
        videoSource = factory.createMediaSource(Uri.parse(videoUrl));
    }

    private void initExoPlayer() {

        binding.exoPlayer.setVisibility(View.VISIBLE);
        binding.placeHolderImage.setVisibility(View.GONE);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        RenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
        binding.exoPlayer.setPlayer(exoPlayer);

        exoPlayer.prepare(videoSource);

        ExoPlayerState exoPlayerState = viewModel.getExoPlayerState();
        if (exoPlayerState != null) {
            exoPlayer.seekTo(exoPlayerState.getWindowIndex(), exoPlayerState.getPosition());
            exoPlayer.setPlayWhenReady(exoPlayerState.isPlayWhenReady());
        } else {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            startExoPlayer();
        }
    }

    private void startExoPlayer() {
        if (videoSource != null && exoPlayer == null) {
            initExoPlayer();
        }
        fullScreenMode();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            startExoPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            stopExoPlayer();
        }
    }

    private void stopExoPlayer() {
        if (exoPlayer != null) {
            int windowIndex = exoPlayer.getCurrentWindowIndex();
            long position = Math.max(0, exoPlayer.getContentPosition());
            boolean playWhenReady = exoPlayer.getPlayWhenReady();
            viewModel.setExoPlayerState(new ExoPlayerState(windowIndex, position, playWhenReady));
        }
        if (fullscreenDialog != null) {
            fullscreenDialog.dismiss();
        }
        releaseExoPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            stopExoPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //have to check both because after api24 onSaveInstanceState is called before stop, so the viewModel won't have state
        //before api24 exoPlayer would be null, because it was already released in onPause
        if (viewModel.getExoPlayerState() != null) {
            outState.putParcelable(EXTRA_EXOPLAYER_STATE, viewModel.getExoPlayerState());
        } else if (exoPlayer != null) {
            int windowIndex = exoPlayer.getCurrentWindowIndex();
            long position = Math.max(0, exoPlayer.getContentPosition());
            boolean playWhenReady = exoPlayer.getPlayWhenReady();
            outState.putParcelable(EXTRA_EXOPLAYER_STATE, new ExoPlayerState(windowIndex, position, playWhenReady));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getRecipeLiveData().removeObservers(this);
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

        isTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (!isTwoPane) {
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
        }


        isFullscreen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTwoPane;
        //binding.exoPlayer.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.cupcake_place_holder));
        setupFullScreen();

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
