package hu.am2.letsbake.data.remote.api;


import java.util.List;

import hu.am2.letsbake.data.remote.model.Recipe;
import io.reactivex.Single;
import retrofit2.http.GET;

public interface BakeApiService {

    @GET("android-baking-app-json")
    Single<List<Recipe>> getAllRecipes();
}
