package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.ExternalRatings

interface OmdbRepository {
    suspend fun getRatings(imdbId: String): Result<ExternalRatings>
}
