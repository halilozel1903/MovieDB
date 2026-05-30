package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonCreditDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "title") val title: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "media_type") val mediaType: String = "",
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "character") val character: String = ""
) {
    fun toDomain(): PersonCredit = PersonCredit(
        id = id,
        title = title ?: name ?: "",
        posterPath = posterPath,
        mediaType = mediaType,
        releaseDate = releaseDate ?: firstAirDate ?: "",
        voteAverage = voteAverage,
        character = character
    )
}

@JsonClass(generateAdapter = true)
data class CombinedCreditsResponseDto(
    @Json(name = "cast") val cast: List<PersonCreditDto> = emptyList()
)
