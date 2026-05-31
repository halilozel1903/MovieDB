package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.PersonInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PersonInfoDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "biography") val biography: String = "",
    @Json(name = "birthday") val birthday: String? = null,
    @Json(name = "deathday") val deathday: String? = null,
    @Json(name = "profile_path") val profilePath: String? = null,
    @Json(name = "known_for_department") val knownForDepartment: String = "",
    @Json(name = "place_of_birth") val placeOfBirth: String? = null,
    @Json(name = "popularity") val popularity: Double = 0.0
) {
    fun toDomain(): PersonInfo = PersonInfo(
        id = id,
        name = name,
        biography = biography,
        birthday = birthday,
        deathday = deathday,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment,
        placeOfBirth = placeOfBirth,
        popularity = popularity
    )
}
