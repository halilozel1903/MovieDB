package com.halil.ozel.moviedb.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Genre> = emptyList(),
    val runtime: Int = 0,
    val originalLanguage: String = ""
)
