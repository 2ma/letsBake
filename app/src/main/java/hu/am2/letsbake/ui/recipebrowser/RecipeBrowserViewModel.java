package hu.am2.letsbake.ui.recipebrowser;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import javax.inject.Inject;

import hu.am2.letsbake.data.Repository;
import hu.am2.letsbake.data.remote.model.Recipe;
import hu.am2.letsbake.domain.Result;
import io.reactivex.disposables.CompositeDisposable;

public class RecipeBrowserViewModel extends ViewModel {

    private static final String TAG = "RecipeBrowserViewModel";

    private MutableLiveData<Result<Recipe>> recipes = new MutableLiveData<>();

    private Repository repository;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Inject
    public RecipeBrowserViewModel(Repository repository) {
        Log.d(TAG, "RecipeBrowserViewModel: create");
        this.repository = repository;
        loadRecipes();
    }

    private void loadRecipes() {
        recipes.postValue(Result.loading());
        compositeDisposable.add(
            repository.getAllRecipes().subscribe(recipesResponse -> recipes.postValue(Result.success(recipesResponse)),
                throwable -> recipes.postValue(Result.error(throwable.getMessage()))
            ));
    }

    public LiveData<Result<Recipe>> getRecipes() {
        return recipes;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

    public void retry() {
        loadRecipes();
    }
}
