package com.halil.ozel.moviedb.domain.model

data class Episode(
    val id: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val airDate: String,
    val voteAverage: Double,
    val runtime: Int
)
