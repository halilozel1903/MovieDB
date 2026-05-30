package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.Cast
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreditResponseDto(
    @Json(name = "cast") val cast: List<CastDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CastDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = "",
    @Json(name = "character") val character: String = "",
    @Json(name = "profile_path") val profilePath: String? = null,
    @Json(name = "order") val order: Int = 0
) {
    fun toDomain(): Cast = Cast(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath,
        order = order
    )
}
