package hu.am2.letsbake.data;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.letsbake.data.local.LocalRepository;
import hu.am2.letsbake.data.remote.RemoteRepository;
import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.Single;

@Singleton
public class Repository {

    private LocalRepository localRepository;
    private RemoteRepository remoteRepository;

    private static final String TAG = "Repository";

    @Inject
    public Repository(LocalRepository localRepository, RemoteRepository remoteRepository) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
    }

    public Single<List<Recipe>> getAllRecipes() {
        /*if (localRepository.getAllRecipes().size() != 0) {
           return Single.fromCallable(() -> localRepository.getAllRecipes());
        } else {
            return remoteRepository.getAllRecipes().flatMap(recipes -> {
                localRepository.addRecipes(recipes);
                return localRepository.getAllRecipes();
            });
        }*/
        return Single.concat(localRepository.getAllRecipes(), getRemoteRecipes()).filter(recipes -> !recipes.isEmpty()).first(Collections
            .emptyList());
    }

    private Single<List<Recipe>> getRemoteRecipes() {
        return remoteRepository.getAllRecipes().doOnSuccess(recipes -> localRepository.addRecipes(recipes));
    }

    public Single<Recipe> getRecipeForId(int id) {
        return localRepository.getRecipeForId(id);
    }
}
