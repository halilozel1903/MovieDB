package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.ExternalRatings
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OmdbResponseDto(
    @Json(name = "Response") val response: String = "False",
    @Json(name = "imdbRating") val imdbRating: String? = null,
    @Json(name = "imdbVotes") val imdbVotes: String? = null,
    @Json(name = "Metascore") val metascore: String? = null,
    @Json(name = "Ratings") val ratings: List<OmdbRatingDto> = emptyList()
) {
    fun toDomain(): ExternalRatings {
        val rt = ratings.firstOrNull { it.source.contains("Rotten", ignoreCase = true) }?.value
        return ExternalRatings(
            imdbRating = imdbRating?.toDoubleOrNull(),
            imdbVotes = imdbVotes,
            rottenTomatoes = rt,
            metacritic = metascore?.toIntOrNull()
        )
    }
}

@JsonClass(generateAdapter = true)
data class OmdbRatingDto(
    @Json(name = "Source") val source: String = "",
    @Json(name = "Value") val value: String = ""
)
