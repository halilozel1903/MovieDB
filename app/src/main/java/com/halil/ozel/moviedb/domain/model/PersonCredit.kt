package com.halil.ozel.moviedb.domain.model

data class PersonCredit(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val mediaType: String,
    val releaseDate: String,
    val voteAverage: Double,
    val character: String
)
