package com.halil.ozel.moviedb.domain.model

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?,
    val order: Int = 0
)
