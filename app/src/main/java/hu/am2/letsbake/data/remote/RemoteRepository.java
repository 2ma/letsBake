package hu.am2.letsbake.data.remote;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.am2.letsbake.data.remote.api.BakeApiService;
import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class RemoteRepository {

    private final BakeApiService bakeApiService;

    @Inject
    public RemoteRepository(BakeApiService bakeApiService) {
        this.bakeApiService = bakeApiService;
    }

    public Single<List<Recipe>> getAllRecipes() {
        return bakeApiService.getAllRecipes().subscribeOn(Schedulers.io());
    }
}
