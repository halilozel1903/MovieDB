package com.halil.ozel.moviedb.data.remote.api

import com.halil.ozel.moviedb.data.remote.dto.CombinedCreditsResponseDto
import com.halil.ozel.moviedb.data.remote.dto.ExternalIdsDto
import com.halil.ozel.moviedb.data.remote.dto.CreditResponseDto
import com.halil.ozel.moviedb.data.remote.dto.GenreListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.MovieDto
import com.halil.ozel.moviedb.data.remote.dto.MovieListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.PersonInfoDto
import com.halil.ozel.moviedb.data.remote.dto.PersonSearchResponseDto
import com.halil.ozel.moviedb.data.remote.dto.SeasonDto
import com.halil.ozel.moviedb.data.remote.dto.TvListResponseDto
import com.halil.ozel.moviedb.data.remote.dto.TvSeriesDto
import com.halil.ozel.moviedb.data.remote.dto.VideoResponseDto
import com.halil.ozel.moviedb.data.remote.dto.WatchProvidersResponseDto
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
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): VideoResponseDto

    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieWatchProviders(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): WatchProvidersResponseDto

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
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TvListResponseDto

    @GET("tv/{tv_id}/videos")
    suspend fun getTvVideos(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): VideoResponseDto

    @GET("tv/{tv_id}/watch/providers")
    suspend fun getTvWatchProviders(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): WatchProvidersResponseDto

    @GET("tv/{tv_id}/external_ids")
    suspend fun getTvExternalIds(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): ExternalIdsDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = true
    ): MovieListResponseDto

    @GET("search/tv")
    suspend fun searchTv(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = true
    ): TvListResponseDto

    @GET("search/person")
    suspend fun searchPersons(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = true
    ): PersonSearchResponseDto

    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String
    ): GenreListResponseDto

    @GET("genre/tv/list")
    suspend fun getTvGenres(
        @Query("api_key") apiKey: String
    ): GenreListResponseDto

    @GET("discover/movie")
    suspend fun discoverMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieListResponseDto

    @GET("discover/tv")
    suspend fun discoverTvByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TvListResponseDto

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String
    ): SeasonDto

    @GET("person/{person_id}/combined_credits")
    suspend fun getCombinedCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): CombinedCreditsResponseDto

    @GET("person/{person_id}")
    suspend fun getPersonInfo(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String
    ): PersonInfoDto
}
