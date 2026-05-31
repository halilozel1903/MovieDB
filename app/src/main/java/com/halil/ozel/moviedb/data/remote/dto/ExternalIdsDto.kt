package com.halil.ozel.moviedb.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExternalIdsDto(
    @Json(name = "imdb_id") val imdbId: String? = null,
    @Json(name = "tvdb_id") val tvdbId: Int? = null
)
