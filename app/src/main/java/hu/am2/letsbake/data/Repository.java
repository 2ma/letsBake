package hu.am2.letsbake.data;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.letsbake.data.local.LocalRepository;
import hu.am2.letsbake.data.remote.RemoteRepository;
import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class Repository {

    private final LocalRepository localRepository;
    private final RemoteRepository remoteRepository;

    @Inject
    public Repository(LocalRepository localRepository, RemoteRepository remoteRepository) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
    }

    public Single<List<Recipe>> getAllRecipes() {
        return Single.concat(localRepository.getAllRecipes(), getRemoteRecipes()).filter(recipes -> !recipes.isEmpty()).first(Collections
            .emptyList());
    }

    private Single<List<Recipe>> getRemoteRecipes() {
        return remoteRepository.getAllRecipes().doOnSuccess(localRepository::addRecipes);
    }

    private Single<Optional<Recipe>> getRecipeForIdRemote(int id) {
        return remoteRepository.getAllRecipes().doOnSuccess(localRepository::addRecipes).flattenAsObservable(recipes -> recipes).filter(recipe ->
            recipe.getId() == id).map(Optional::new).first(new Optional<>(null));
    }

    public Single<Optional<Recipe>> getRecipeForId(int id) {
        return Single.concat(localRepository.getRecipeForId(id), getRecipeForIdRemote(id)).filter(recipe -> !recipe.isEmpty()).first(new Optional<>
            (null)).subscribeOn(Schedulers.io());
    }
}
