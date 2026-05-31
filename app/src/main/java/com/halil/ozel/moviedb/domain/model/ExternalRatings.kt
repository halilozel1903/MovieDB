package com.halil.ozel.moviedb.domain.model

data class ExternalRatings(
    val imdbRating: Double?,
    val imdbVotes: String?,
    val rottenTomatoes: String?,
    val metacritic: Int?
)
