package hu.am2.letsbake;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import hu.am2.letsbake.di.DaggerAppComponent;


public class App extends Application implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjectorActivity;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjectorFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder()
            .context(getApplicationContext())
            .build()
            .inject(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjectorActivity;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjectorFragment;
    }
}
