package hu.am2.letsbake;


import android.support.test.runner.AndroidJUnitRunner;

import com.squareup.rx2.idler.Rx2Idler;

import io.reactivex.plugins.RxJavaPlugins;

//based off of https://github.com/tir38/RxIdler/tree/add_sample_app
public class RxJavaTestRunner extends AndroidJUnitRunner {

    @Override
    public void onStart() {
        super.onStart();

        RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("RxJava 2.x IO Scheduler"));
    }
}
