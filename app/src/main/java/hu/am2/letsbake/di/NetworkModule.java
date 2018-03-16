package hu.am2.letsbake.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hu.am2.letsbake.data.remote.api.BakeApiService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private static final String BASE_URL = "http://go.udacity.com";

    @Provides
    @Singleton
    Retrofit providesRetrofit() {
        return new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    @Provides
    @Singleton
    BakeApiService providesBakeApiService(Retrofit retrofit) {
        return retrofit.create(BakeApiService.class);
    }
}
