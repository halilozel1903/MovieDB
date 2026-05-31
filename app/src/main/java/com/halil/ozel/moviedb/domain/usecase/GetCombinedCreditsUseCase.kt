package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.halil.ozel.moviedb.domain.repository.PersonRepository
import javax.inject.Inject

class GetCombinedCreditsUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(personId: Int): Result<List<PersonCredit>> =
        repository.getCombinedCredits(personId)
}
