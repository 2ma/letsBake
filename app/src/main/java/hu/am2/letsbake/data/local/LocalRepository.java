package hu.am2.letsbake.data.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.letsbake.data.Optional;
import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.Single;

@Singleton
public class LocalRepository {

    private final HashMap<Integer, Recipe> localCache = new HashMap<>();

    @Inject
    public LocalRepository() {
    }

    public Single<List<Recipe>> getAllRecipes() {
        return Single.fromCallable(() -> new ArrayList<>(localCache.values()));
    }

    public void addRecipes(List<Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            final Recipe recipe = recipes.get(i);
            localCache.put(recipe.getId(), recipe);
        }
    }

    public Single<Optional<Recipe>> getRecipeForId(int id) {
        return Single.fromCallable(() -> {
            if (localCache.containsKey(id)) {
                return new Optional<>(localCache.get(id));
            } else {
                return new Optional<>(null);
            }
        });
    }

}
