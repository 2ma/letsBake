package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;
import android.util.Log;

import javax.inject.Inject;

import hu.am2.letsbake.data.Repository;
import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.disposables.CompositeDisposable;

public class RecipeDetailViewModel extends ViewModel {

    private final Repository repository;

    private final MutableLiveData<Recipe> recipe = new MutableLiveData<>();

    //private MutableLiveData<Integer> recipeStep = new MutableLiveData<>();

    private final MutableLiveData<Pair<Integer, Recipe>> recipeStep = new MutableLiveData<>();

    private final MutableLiveData<Integer> recipeStepNumber = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Pair<Integer, Long>> playerPosition = new MutableLiveData<>();

    private boolean isTwoPane;

    private static final String TAG = "RecipeDetailViewModel";


    @Inject
    public RecipeDetailViewModel(Repository repository) {
        this.repository = repository;
        Log.d(TAG, "RecipeDetailViewModel: created");
    }

    public void setRecipeId(int id) {
        compositeDisposable.add(repository.getRecipeForId(id).subscribe(recipe1 -> {
            if (!recipe1.isEmpty()) {
                recipe.postValue(recipe1.get());
            }
        }));
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }

    public LiveData<Pair<Integer, Recipe>> getRecipeStep() {
        return recipeStep;
    }

    public LiveData<Pair<Integer, Long>> getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(int index, long position) {
        playerPosition.setValue(new Pair<>(index, position));
    }

    public void setRecipeStep(int step) {
        recipeStepNumber.setValue(step);
    }

    public LiveData<Integer> getRecipeStepNumber() {
        return recipeStepNumber;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public boolean isTwoPane() {
        return isTwoPane;
    }

    public void setTwoPane(boolean twoPane) {
        isTwoPane = twoPane;
    }

    public void nextClick() {
        Pair<Integer, Recipe> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first < step.second.getSteps().size()) {
            int next = step.first + 1;
            Pair<Integer, Recipe> nextStep = new Pair<>(next, recipe.getValue());
            recipeStep.setValue(nextStep);
        }
        playerPosition.setValue(null);
    }

    public void prevClick() {
        Pair<Integer, Recipe> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first > 0) {
            int prev = step.first - 1;
            Pair<Integer, Recipe> prevStep = new Pair<>(prev, recipe.getValue());
            recipeStep.setValue(prevStep);
        }
        playerPosition.setValue(null);
    }

    public void setStep(int step) {
        Recipe r = recipe.getValue();
        if (r != null) {
            recipeStep.setValue(new Pair<>(step, r));
        }
    }

    public void setRecipeStepAndId(int recipeId, int step) {

        Recipe r = recipe.getValue();

        if (r == null || r.getId() != recipeId) {
            compositeDisposable.add(repository.getRecipeForId(recipeId).subscribe(recipe1 ->
                {
                    if (!recipe1.isEmpty()) {
                        recipe.postValue(recipe1.get());
                        recipeStep.postValue(new Pair<>(step, recipe1.get()));
                    }
                }
            ));
        }
    }

    public int getRecipeId() {
        return recipe.getValue().getId();
    }
}
