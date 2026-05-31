package com.halil.ozel.moviedb.domain.repository

import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.halil.ozel.moviedb.domain.model.PersonInfo
import com.halil.ozel.moviedb.domain.model.PersonSearchResult

interface PersonRepository {
    suspend fun getCombinedCredits(personId: Int): Result<List<PersonCredit>>
    suspend fun getPersonInfo(personId: Int): Result<PersonInfo>
    suspend fun searchPersons(query: String, page: Int = 1): Result<List<PersonSearchResult>>
}
