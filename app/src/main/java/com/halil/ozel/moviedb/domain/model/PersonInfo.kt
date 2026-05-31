package com.halil.ozel.moviedb.domain.model

data class PersonInfo(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val deathday: String?,
    val profilePath: String?,
    val knownForDepartment: String,
    val placeOfBirth: String?,
    val popularity: Double
)
