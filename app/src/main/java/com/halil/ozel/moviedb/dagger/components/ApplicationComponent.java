package com.halil.ozel.moviedb.dagger.components;

import com.halil.ozel.moviedb.App;
import com.halil.ozel.moviedb.ui.home.adapters.MovieAdapter;
import com.halil.ozel.moviedb.dagger.AppScope;
import com.halil.ozel.moviedb.dagger.modules.ApplicationModule;
import com.halil.ozel.moviedb.dagger.modules.HttpClientModule;
import com.halil.ozel.moviedb.ui.detail.adapters.MovieCastAdapter;
import com.halil.ozel.moviedb.ui.detail.MovieDetailActivity;
import com.halil.ozel.moviedb.ui.detail.CastDetailActivity;
import com.halil.ozel.moviedb.ui.detail.AllCastActivity;
import com.halil.ozel.moviedb.ui.detail.TvSeriesDetailActivity;
import com.halil.ozel.moviedb.ui.detail.adapters.RecommendMovieAdapter;
import com.halil.ozel.moviedb.ui.home.activity.MainActivity;
import com.halil.ozel.moviedb.ui.home.activity.AllMoviesActivity;
import com.halil.ozel.moviedb.ui.home.activity.AllTvActivity;
import com.halil.ozel.moviedb.ui.home.fragments.MoviesFragment;
import com.halil.ozel.moviedb.ui.home.fragments.TvSeriesFragment;
import com.halil.ozel.moviedb.ui.home.fragments.FavoriteFragment;
import com.halil.ozel.moviedb.ui.home.fragments.SettingsFragment;
import com.halil.ozel.moviedb.ui.home.adapters.TvSeriesAdapter;
import com.halil.ozel.moviedb.ui.home.fragments.SearchFragment;

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
    void inject(AllMoviesActivity allMoviesActivity);
    void inject(AllTvActivity allTvActivity);

    void inject(MovieDetailActivity movieDetailActivity);
    void inject(TvSeriesDetailActivity tvSeriesDetailActivity);

    void inject(AllCastActivity allCastActivity);

    void inject(CastDetailActivity castDetailActivity);

    void inject(MovieAdapter movieAdapter);

    void inject(RecommendMovieAdapter recommendMovieAdapter);

    void inject(MovieCastAdapter movieCastAdapter);

    void inject(MoviesFragment moviesFragment);
    void inject(TvSeriesFragment tvSeriesFragment);
    void inject(FavoriteFragment favoriteFragment);
    void inject(SettingsFragment settingsFragment);
    void inject(TvSeriesAdapter tvSeriesAdapter);
    void inject(SearchFragment searchFragment);


}
