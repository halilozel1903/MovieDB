package com.halil.ozel.moviedb.di

import com.halil.ozel.moviedb.data.repository.FavoritesRepositoryImpl
import com.halil.ozel.moviedb.data.repository.MovieRepositoryImpl
import com.halil.ozel.moviedb.data.repository.SearchRepositoryImpl
import com.halil.ozel.moviedb.data.repository.TvRepositoryImpl
import com.halil.ozel.moviedb.domain.repository.FavoritesRepository
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import com.halil.ozel.moviedb.domain.repository.SearchRepository
import com.halil.ozel.moviedb.domain.repository.TvRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindTvRepository(impl: TvRepositoryImpl): TvRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}
