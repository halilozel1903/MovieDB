package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.PersonCredit

interface PersonRepository {
    suspend fun getCombinedCredits(personId: Int): Result<List<PersonCredit>>
}
