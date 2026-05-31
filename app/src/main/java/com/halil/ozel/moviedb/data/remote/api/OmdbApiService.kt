package com.halil.ozel.moviedb.data.remote.api

import com.halil.ozel.moviedb.data.remote.dto.OmdbResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    /* OMDB base URL = https://www.omdbapi.com/
       endpoint "." → stays at root, no double-slash issue */
    @GET(".")
    suspend fun getRatings(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String
    ): OmdbResponseDto
}
