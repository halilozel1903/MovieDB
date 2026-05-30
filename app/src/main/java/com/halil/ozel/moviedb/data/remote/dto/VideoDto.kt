package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.Video
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "key") val key: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "site") val site: String = "",
    @Json(name = "type") val type: String = "",
    @Json(name = "official") val official: Boolean = false
) {
    fun toDomain(): Video = Video(
        id = id,
        key = key,
        name = name,
        site = site,
        type = type,
        official = official
    )
}

@JsonClass(generateAdapter = true)
data class VideoResponseDto(
    @Json(name = "results") val results: List<VideoDto> = emptyList()
)
