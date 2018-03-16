package hu.am2.letsbake.ui.recipe;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;

import javax.inject.Inject;

import hu.am2.letsbake.data.Repository;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.data.remote.model.RecipeStep;
import io.reactivex.disposables.CompositeDisposable;

public class RecipeDetailViewModel extends ViewModel {

    private Repository repository;

    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();

    //private MutableLiveData<Integer> recipeStep = new MutableLiveData<>();

    private MutableLiveData<Pair<Integer, RecipeStep>> recipeStep = new MutableLiveData<>();

    private MutableLiveData<Integer> recipeStepNumber = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Inject
    public RecipeDetailViewModel(Repository repository) {
        this.repository = repository;
    }

    public void setRecipeId(int id) {
        compositeDisposable.add(repository.getRecipeForId(id).subscribe(recipe1 -> recipe.postValue(recipe1)));
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }

    public LiveData<Pair<Integer, RecipeStep>> getRecipeStep() {
        return recipeStep;
    }

    public void setRecipeStep(int step) {
        recipeStepNumber.setValue(step);
    }

    public LiveData<Integer> getRecipeStepNumber() {
        return recipeStepNumber;
    }

    public int getMaximumSteps() {
        return recipe.getValue().getSteps().size() - 1;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void nextClick() {
        Pair<Integer, RecipeStep> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first < getMaximumSteps()) {
            int next = step.first + 1;
            Pair<Integer, RecipeStep> nextStep = new Pair<>(next, recipe.getValue().getSteps().get(next));
            recipeStep.setValue(nextStep);
        }
    }

    public void prevClick() {
        Pair<Integer, RecipeStep> step = recipeStep.getValue();
        if (step != null && step.first != null && step.first > 0) {
            int prev = step.first - 1;
            Pair<Integer, RecipeStep> prevStep = new Pair<>(prev, recipe.getValue().getSteps().get(prev));
            recipeStep.setValue(prevStep);
        }
    }

    public void setStep(int step) {
        Pair<Integer, RecipeStep> stepPair = new Pair<>(step, recipe.getValue().getSteps().get(step));
        recipeStep.setValue(stepPair);
    }
}
