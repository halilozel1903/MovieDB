package com.halil.ozel.moviedb.data.repository

import com.halil.ozel.moviedb.BuildConfig
import com.halil.ozel.moviedb.data.remote.api.OmdbApiService
import com.halil.ozel.moviedb.domain.model.ExternalRatings
import com.halil.ozel.moviedb.domain.repository.OmdbRepository
import javax.inject.Inject
import javax.inject.Named

class OmdbRepositoryImpl @Inject constructor(
    @Named("omdb") private val apiService: OmdbApiService
) : OmdbRepository {

    private val apiKey = BuildConfig.OMDB_API_KEY

    override suspend fun getRatings(imdbId: String): Result<ExternalRatings> = runCatching {
        val dto = apiService.getRatings(imdbId, apiKey)
        if (!dto.response.trim().equals("True", ignoreCase = true)) error("OMDB: no result for $imdbId")
        dto.toDomain()
    }
}
