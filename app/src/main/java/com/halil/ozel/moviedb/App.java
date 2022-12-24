package com.halil.ozel.moviedb;

import android.app.Application;

import com.halil.ozel.moviedb.dagger.components.ApplicationComponent;
import com.halil.ozel.moviedb.dagger.components.DaggerApplicationComponent;
import com.halil.ozel.moviedb.dagger.modules.ApplicationModule;
import com.halil.ozel.moviedb.dagger.modules.HttpClientModule;

import timber.log.Timber;

public class App extends Application {

    private static App instance;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        // Creates Dagger Graph
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .httpClientModule(new HttpClientModule())
                .build();

        mApplicationComponent.inject(this);
    }

    public static App instance() {
        return instance;
    }

    public ApplicationComponent appComponent() {
        return mApplicationComponent;
    }
}