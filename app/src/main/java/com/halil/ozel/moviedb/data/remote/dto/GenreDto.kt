package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.Genre
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "name") val name: String = ""
) {
    fun toDomain(): Genre = Genre(id = id, name = name)
}

@JsonClass(generateAdapter = true)
data class GenreListResponseDto(
    @Json(name = "genres") val genres: List<GenreDto> = emptyList()
)
