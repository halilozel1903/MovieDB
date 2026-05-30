package com.halil.ozel.moviedb.data.remote.api

import com.halil.ozel.moviedb.data.remote.dto.CreditResponseDto
import com.halil.ozel.moviedb.data.remote.dto.GenreListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.MovieDto
import com.halil.ozel.moviedb.data.remote.dto.MovieListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.TvListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.TvSeriesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDbApiService {

    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): CreditResponseDto

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): MovieListResponseDto

    @GET("tv/popular")
    suspend fun getPopularTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("tv/airing_today")
    suspend fun getAiringTodayTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("tv/{tv_id}")
    suspend fun getTvDetail(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): TvSeriesDto

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): CreditResponseDto

    @GET("tv/{tv_id}/recommendations")
    suspend fun getTvRecommendations(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): TvListResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("search/tv")
    suspend fun searchTv(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String
    ): GenreListResponseDto

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("api_key") apiKey: String
    ): GenreListResponseDto
}
