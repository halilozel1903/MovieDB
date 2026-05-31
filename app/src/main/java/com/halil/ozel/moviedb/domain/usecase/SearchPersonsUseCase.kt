package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.halil.ozel.moviedb.domain.repository.PersonRepository
import javax.inject.Inject

class SearchPersonsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<PersonSearchResult>> =
        repository.searchPersons(query, page)
}
