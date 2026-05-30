package com.halil.ozel.moviedb.domain.model

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean
)
