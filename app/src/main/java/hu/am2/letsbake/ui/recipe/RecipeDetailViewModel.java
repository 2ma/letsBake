package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;
import android.util.Log;

import javax.inject.Inject;

import hu.am2.letsbake.data.Repository;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.data.remote.model.RecipeStep;
import hu.am2.letsbake.domain.Result;
import hu.am2.letsbake.domain.ResultStatus;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RecipeDetailViewModel extends ViewModel {

    private final Repository repository;

    private final MutableLiveData<Result<Recipe>> recipe = new MutableLiveData<>();

    private final MutableLiveData<Pair<Integer, Recipe>> recipeStep = new MutableLiveData<>();

    private final MutableLiveData<Integer> recipeStepActivityTracker = new MutableLiveData<>();

    private final MutableLiveData<Integer> recipeStepDetailListFragmentTracker = new MutableLiveData<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Pair<Integer, Long> playerPosition = null;

    private static final String TAG = "RecipeDetailViewModel";


    @Inject
    public RecipeDetailViewModel(Repository repository) {
        this.repository = repository;
        Log.d(TAG, "RecipeDetailViewModel: created");
    }

    void setRecipeId(int id) {
        recipe.postValue(Result.loading());
        compositeDisposable.add(repository.getRecipeForId(id).subscribeOn(Schedulers.io()).subscribe(recipe1 -> {
            if (!recipe1.isEmpty()) {
                recipe.postValue(Result.success(recipe1.get()));
            } else {
                recipe.postValue(Result.error("No recipe"));
            }
        }, throwable -> recipe.postValue(Result.error(throwable.getMessage()))));
    }

    LiveData<Integer> getRecipeStepDetailListFragmentTracker() {
        return recipeStepDetailListFragmentTracker;
    }

    void setRecipeStepDetailListFragmentTracker(int step) {
        recipeStepDetailListFragmentTracker.setValue(step);
    }

    LiveData<Result<Recipe>> getRecipeLiveData() {
        return recipe;
    }

    LiveData<Pair<Integer, Recipe>> getRecipeStepLiveData() {
        return recipeStep;
    }

    Pair<Integer, Long> getPlayerPosition() {
        return playerPosition;
    }

    void setPlayerPosition(int index, long position) {
        playerPosition = new Pair<>(index, position);
    }

    void setRecipeStepActivityTracker(int step) {
        recipeStepActivityTracker.setValue(step);
    }

    LiveData<Integer> getRecipeStepActivityTrackerLiveData() {
        return recipeStepActivityTracker;
    }

    public RecipeStep getRecipeStep() {
        if (recipeStep.getValue() != null) {
            Pair<Integer, Recipe> stepPair = recipeStep.getValue();
            return stepPair.second.getSteps().get(stepPair.first);
        } else {
            return null;
        }
    }

    int getRecipeStepNumber() {
        if (recipeStep.getValue() != null) {
            return recipeStep.getValue().first;
        } else {
            return -1;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        Log.d(TAG, "onCleared: RecipeDetailViewModel");
    }

    void nextClick() {
        Pair<Integer, Recipe> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first < step.second.getSteps().size()
            && recipe.getValue() != null && recipe.getValue().status == ResultStatus.SUCCESS) {
            int next = step.first + 1;
            Pair<Integer, Recipe> nextStep = new Pair<>(next, recipe.getValue().data);
            recipeStep.setValue(nextStep);
        }
        playerPosition = null;
    }

    void prevClick() {
        Pair<Integer, Recipe> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first > 0 && recipe.getValue() != null
            && recipe.getValue().status == ResultStatus.SUCCESS) {
            int prev = step.first - 1;
            Pair<Integer, Recipe> prevStep = new Pair<>(prev, recipe.getValue().data);
            recipeStep.setValue(prevStep);
        }
        playerPosition = null;
    }

    void setStep(int step) {
        Result<Recipe> r = recipe.getValue();
        if (r != null && r.status == ResultStatus.SUCCESS) {
            recipeStep.setValue(new Pair<>(step, r.data));
        }
    }

    void setRecipeStepAndId(int recipeId, int step) {

        Result<Recipe> r = recipe.getValue();

        if (r == null || r.status == ResultStatus.SUCCESS && r.data.getId() != recipeId) {
            recipe.postValue(Result.loading());
            compositeDisposable.add(repository.getRecipeForId(recipeId).subscribe(recipe1 ->
                {
                    if (!recipe1.isEmpty()) {
                        recipe.postValue(Result.success(recipe1.get()));
                        recipeStep.postValue(new Pair<>(step, recipe1.get()));
                    } else {
                        recipe.postValue(Result.error("No recipe"));
                    }
                },
                throwable -> recipe.postValue(Result.error(throwable.getMessage()))
            ));
        }
    }

    int getRecipeId() {

        if (recipe.getValue() == null || recipe.getValue().status != ResultStatus.SUCCESS) {
            return -1;
        }

        return recipe.getValue().data.getId();
    }
}
