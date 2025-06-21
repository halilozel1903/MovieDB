package com.halil.ozel.moviedb.data.Api;


import com.halil.ozel.moviedb.dagger.modules.HttpClientModule;
import com.halil.ozel.moviedb.data.models.ResponseCreditDetail;
import com.halil.ozel.moviedb.data.models.ResponseMovieDetail;
import com.halil.ozel.moviedb.data.models.ResponseNowPlaying;
import com.halil.ozel.moviedb.data.models.PersonDetail;
import com.halil.ozel.moviedb.data.models.ResponsePerson;
import com.halil.ozel.moviedb.data.models.ResponseTvSeries;
import com.halil.ozel.moviedb.data.models.ResponseGenreList;
import com.halil.ozel.moviedb.data.models.ResponseKeyword;
import com.halil.ozel.moviedb.data.models.ResponseTvSeriesDetail;
import com.halil.ozel.moviedb.data.models.ResponsePersonMovieCredits;
import com.halil.ozel.moviedb.data.models.ResponsePersonTvCredits;
import com.halil.ozel.moviedb.data.models.ResponseSeasonDetail;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.rxjava3.core.Observable;

public interface TMDbAPI {

    String IMAGE_BASE_URL_500 = "https://image.tmdb.org/t/p/w500";
    String IMAGE_BASE_URL_1280 = "https://image.tmdb.org/t/p/w1280";

    String TMDb_API_KEY = "45dfdbd49fa1f1da1f5b75fd60217433";

    @GET(HttpClientModule.NOW_PLAYING)
    Observable<ResponseNowPlaying> getNowPlaying(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.POPULAR)
    Observable<ResponseNowPlaying> getPopularMovie(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TOP_RATED)
    Observable<ResponseNowPlaying> getTopRatedMovie(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.UPCOMING)
    Observable<ResponseNowPlaying> getUpcomingMovie(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TV_POPULAR)
    Observable<ResponseTvSeries> getTvPopular(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TV_TOP_RATED)
    Observable<ResponseTvSeries> getTvTopRated(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TV_AIRING_TODAY)
    Observable<ResponseTvSeries> getTvAiringToday(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TV_ON_THE_AIR)
    Observable<ResponseTvSeries> getTvOnTheAir(
            @Query("api_key") String api_key,
            @Query("page") int page
    );

    @GET(HttpClientModule.TV_DETAILS + "{tv_id}")
    Observable<ResponseTvSeriesDetail> getTvSeriesDetail(
            @Path("tv_id") int tvId,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.TV_DETAILS + "{tv_id}/credits")
    Observable<ResponseCreditDetail> getTvCastDetail(
            @Path("tv_id") int tvId,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.TV_DETAILS + "{tv_id}/recommendations")
    Observable<ResponseTvSeries> getTvRecommendations(
            @Path("tv_id") int tvId,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.TV_DETAILS + "{tv_id}/season/{season_number}")
    Observable<ResponseSeasonDetail> getSeasonDetail(
            @Path("tv_id") int tvId,
            @Path("season_number") int seasonNumber,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.GENRE_MOVIE)
    Observable<ResponseGenreList> getMovieGenres(
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.GENRE_TV)
    Observable<ResponseGenreList> getTvGenres(
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.SEARCH_KEYWORD)
    Observable<ResponseKeyword> searchKeyword(
            @Query("api_key") String api_key,
            @Query("query") String query,
            @Query("page") int page
    );

    @GET(HttpClientModule.SEARCH_MOVIE)
    Observable<ResponseNowPlaying> searchMovie(
            @Query("api_key") String api_key,
            @Query("query") String query,
            @Query("page") int page
    );

    @GET(HttpClientModule.SEARCH_TV)
    Observable<ResponseTvSeries> searchTv(
            @Query("api_key") String api_key,
            @Query("query") String query,
            @Query("page") int page
    );

    @GET(HttpClientModule.SEARCH_PERSON)
    Observable<ResponsePerson> searchPerson(
            @Query("api_key") String api_key,
            @Query("query") String query,
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

    @GET(HttpClientModule.PERSON_DETAILS + "{person_id}")
    Observable<PersonDetail> getPersonDetail(
            @Path("person_id") int person_id,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.PERSON_DETAILS + "{person_id}/movie_credits")
    Observable<ResponsePersonMovieCredits> getPersonMovieCredits(
            @Path("person_id") int person_id,
            @Query("api_key") String api_key
    );

    @GET(HttpClientModule.PERSON_DETAILS + "{person_id}/tv_credits")
    Observable<ResponsePersonTvCredits> getPersonTvCredits(
            @Path("person_id") int person_id,
            @Query("api_key") String api_key
    );
}
