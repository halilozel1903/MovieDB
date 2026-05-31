package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonSearchDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "profile_path") val profilePath: String? = null,
    @Json(name = "known_for_department") val knownForDepartment: String = "",
    @Json(name = "popularity") val popularity: Double = 0.0
) {
    fun toDomain(): PersonSearchResult = PersonSearchResult(
        id = id,
        name = name,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment,
        popularity = popularity
    )
}

@JsonClass(generateAdapter = true)
data class PersonSearchResponseDto(
    @Json(name = "results") val results: List<PersonSearchDto> = emptyList()
)
