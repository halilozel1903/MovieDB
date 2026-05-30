package com.halil.ozel.moviedb.domain.model

data class Person(
    val id: Int,
    val name: String,
    val biography: String,
    val birthday: String?,
    val profilePath: String?,
    val popularity: Double,
    val knownForDepartment: String
)
