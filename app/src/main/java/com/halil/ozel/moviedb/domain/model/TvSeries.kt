package com.halil.ozel.moviedb.domain.model

data class TvSeries(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val firstAirDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Genre> = emptyList(),
    val numberOfSeasons: Int = 0,
    val numberOfEpisodes: Int = 0,
    val originalLanguage: String = "",
    val seasons: List<Season> = emptyList(),
    val imdbId: String? = null
)
