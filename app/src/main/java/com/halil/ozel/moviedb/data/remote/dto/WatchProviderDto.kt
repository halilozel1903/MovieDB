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
    @Json(name = "flatrate") val flatrate: List<WatchProviderItemDto>? = null,
    @Json(name = "rent")     val rent:     List<WatchProviderItemDto>? = null,
    @Json(name = "buy")      val buy:      List<WatchProviderItemDto>? = null,
    @Json(name = "free")     val free:     List<WatchProviderItemDto>? = null
) {
    /** All providers, deduplicated by id, sorted by displayPriority */
    fun allProviders(): List<WatchProviderItemDto> =
        ((flatrate ?: emptyList()) + (free ?: emptyList()) +
         (rent ?: emptyList()) + (buy ?: emptyList()))
            .distinctBy { it.providerId }
            .sortedBy { it.displayPriority }
            .take(8)
}

data class WatchProvidersResponseDto(
    @Json(name = "results") val results: Map<String, WatchProviderCountryDto> = emptyMap()
)
