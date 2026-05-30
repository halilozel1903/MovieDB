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
    @Json(name = "overview") val overview: String = "",
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "episode_count") val episodeCount: Int = 0,
    @Json(name = "air_date") val airDate: String = "",
    @Json(name = "episodes") val episodes: List<EpisodeDto> = emptyList()
) {
    fun toDomain(): Season = Season(
        id = id,
        seasonNumber = seasonNumber,
        name = name,
        overview = overview,
        posterPath = posterPath,
        episodeCount = if (episodeCount > 0) episodeCount else episodes.size,
        airDate = airDate,
        episodes = episodes.map { it.toDomain() }
    )
}

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "overview") val overview: String = "",
    @Json(name = "still_path") val stillPath: String? = null,
    @Json(name = "episode_number") val episodeNumber: Int = 0,
    @Json(name = "season_number") val seasonNumber: Int = 0,
    @Json(name = "air_date") val airDate: String = "",
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "runtime") val runtime: Int = 0
) {
    fun toDomain(): Episode = Episode(
        id = id,
        name = name,
        overview = overview,
        stillPath = stillPath,
        episodeNumber = episodeNumber,
        seasonNumber = seasonNumber,
        airDate = airDate,
        voteAverage = voteAverage,
        runtime = runtime
    )
}
