package com.halil.ozel.moviedb.data.repository

import com.halil.ozel.moviedb.BuildConfig
import com.halil.ozel.moviedb.data.remote.api.TMDbApiService
import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.halil.ozel.moviedb.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val apiService: TMDbApiService
) : PersonRepository {

    private val apiKey = BuildConfig.TMDB_API_KEY

    override suspend fun getCombinedCredits(personId: Int): Result<List<PersonCredit>> = runCatching {
        apiService.getCombinedCredits(personId, apiKey).cast
            .filter { it.mediaType in listOf("movie", "tv") && it.posterPath != null }
            .sortedByDescending { it.voteAverage }
            .take(40)
            .map { it.toDomain() }
    }
}
