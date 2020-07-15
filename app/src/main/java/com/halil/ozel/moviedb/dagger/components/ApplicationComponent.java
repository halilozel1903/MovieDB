package com.halil.ozel.moviedb.dagger.components;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;
import com.halil.ozel.moviedb.dagger.AppScope;
import com.halil.ozel.moviedb.dagger.modules.ApplicationModule;
import com.halil.ozel.moviedb.dagger.modules.HttpClientModule;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;
import com.halil.ozel.moviedb.ui.detail.MovieDetailActivity;
import com.halil.ozel.moviedb.ui.detail.adapters.RecommendMovieAdapter;
import com.halil.ozel.moviedb.ui.home.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@AppScope
@Singleton
@Component(modules = {
        ApplicationModule.class,
        HttpClientModule.class,
})

public interface ApplicationComponent {

    void inject(App app);

    void inject(MainActivity mainActivity);

    void inject(MovieDetailActivity movieDetailActivity);

    void inject(MovieAdapter movieAdapter);

    void inject(RecommendMovieAdapter recommendMovieAdapter);

    void inject(MovieCastAdapter movieCastAdapter);


}
