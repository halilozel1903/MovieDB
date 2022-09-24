package com.halil.ozel.moviedb.data.Api;


import com.halil.ozel.moviedb.dagger.modules.HttpClientModule;
import com.halil.ozel.moviedb.data.models.ResponseCreditDetail;
import com.halil.ozel.moviedb.data.models.ResponseMovieDetail;
import com.halil.ozel.moviedb.data.models.ResponseNowPlaying;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TMDbAPI {

    String IMAGE_BASE_URL_500 = "https://image.tmdb.org/t/p/w500";
    String IMAGE_BASE_URL_1280 = "https://image.tmdb.org/t/p/w1280";

    String TMDb_API_KEY = "45dfdbd49fa1f1da1f5b75fd60217433";

    @GET(HttpClientModule.NOW_ON_PLAYING)
    Observable<ResponseNowPlaying> getNowPlaying(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.POPULAR)
    Observable<ResponseNowPlaying> getPopularMovie(
            @Query("api_key") String api_key,
            @Query("page") int page
    );


    @GET(HttpClientModule.MOVIE_DETAILS + "{movie_id}")
    Observable<ResponseMovieDetail> getMovieDetail(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.MOVIE_DETAILS + "{movie_id}/credits")
    Observable<ResponseCreditDetail> getCreditDetail(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.MOVIE_DETAILS + "{movie_id}/recommendations")
    Observable<ResponseNowPlaying> getRecommendDetail(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key
    );
}
