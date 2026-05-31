package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.PersonInfo
import com.halil.ozel.moviedb.domain.repository.PersonRepository
import javax.inject.Inject

class GetPersonInfoUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend operator fun invoke(personId: Int): Result<PersonInfo> =
        repository.getPersonInfo(personId)
}
