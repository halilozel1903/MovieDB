package com.halil.ozel.moviedb.domain.model

data class PersonSearchResult(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val knownForDepartment: String,
    val popularity: Double
)
