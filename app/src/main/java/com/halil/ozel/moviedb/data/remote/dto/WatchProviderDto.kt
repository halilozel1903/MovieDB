package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.squareup.moshi.Json

// No @JsonClass(generateAdapter = true) here — uses KotlinJsonAdapterFactory
// because Map<String, T> is not supported by code generation
data class WatchProviderItemDto(
    @Json(name = "provider_id") val providerId: Int = 0,
    @Json(name = "provider_name") val providerName: String = "",
    @Json(name = "logo_path") val logoPath: String = "",
    @Json(name = "display_priority") val displayPriority: Int = 0
) {
    fun toDomain(): WatchProvider = WatchProvider(
        id = providerId,
        name = providerName,
        logoPath = logoPath,
        displayPriority = displayPriority
    )
}

data class WatchProviderCountryDto(
    @Json(name = "flatrate") val flatrate: List<WatchProviderItemDto>? = null
)

data class WatchProvidersResponseDto(
    @Json(name = "results") val results: Map<String, WatchProviderCountryDto> = emptyMap()
)
