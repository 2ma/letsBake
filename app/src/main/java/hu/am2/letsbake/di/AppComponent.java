package hu.am2.letsbake.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import hu.am2.letsbake.App;

@Singleton
@Component(modules = {AndroidInjectionModule.class, ActivityModule.class, ViewModelModule.class, NetworkModule.class, FragmentModule.class})
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder context(Context context);
    }

    void inject(App app);
}
