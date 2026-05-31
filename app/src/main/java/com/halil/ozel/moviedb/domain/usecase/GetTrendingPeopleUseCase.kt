package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.halil.ozel.moviedb.domain.repository.PersonRepository
import javax.inject.Inject

class GetTrendingPeopleUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(): Result<List<PersonSearchResult>> = repository.getTrendingPeople()
}
