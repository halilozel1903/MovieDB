package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.Episode
import com.halil.ozel.moviedb.domain.model.Season
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SeasonDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "season_number") val seasonNumber: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "episode_count") val episodeCount: Int? = null,
    @Json(name = "air_date") val airDate: String? = null,
    @Json(name = "episodes") val episodes: List<EpisodeDto> = emptyList()
) {
    fun toDomain(): Season = Season(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview ?: "",
        posterPath = posterPath,
        episodeCount = (episodeCount ?: 0).let { if (it > 0) it else episodes.size },
        airDate = airDate ?: "",
        episodes = episodes.map { it.toDomain() }
    )
}

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "still_path") val stillPath: String? = null,
    @Json(name = "episode_number") val episodeNumber: Int? = null,
    @Json(name = "season_number") val seasonNumber: Int? = null,
    @Json(name = "air_date") val airDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = null,
    @Json(name = "runtime") val runtime: Int? = null
) {
    fun toDomain(): Episode = Episode(
        id = id,
        name = name,
        overview = overview ?: "",
        stillPath = stillPath,
        episodeNumber = episodeNumber ?: 0,
        seasonNumber = seasonNumber ?: 0,
        airDate = airDate ?: "",
        voteAverage = voteAverage ?: 0.0,
        runtime = runtime ?: 0
    )
}
