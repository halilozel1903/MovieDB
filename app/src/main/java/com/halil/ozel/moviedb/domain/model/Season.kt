package com.halil.ozel.moviedb.domain.model

data class Season(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val episodeCount: Int,
    val airDate: String,
    val episodes: List<Episode> = emptyList()
)
