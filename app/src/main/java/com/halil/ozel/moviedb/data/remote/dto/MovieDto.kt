package com.halil.ozel.moviedb.data.remote.dto

import com.halil.ozel.moviedb.domain.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieListResponseDto(
    @Json(name = "results") val results: List<MovieDto> = emptyList(),
    @Json(name = "page") val page: Int = 1,
    @Json(name = "total_pages") val totalPages: Int = 1
)

@JsonClass(generateAdapter = true)
data class MovieDto(
    @Json(name = "id") val id: Int = 0,
    @Json(name = "title") val title: String = "",
    @Json(name = "overview") val overview: String = "",
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String = "",
    @Json(name = "popularity") val popularity: Double = 0.0,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "vote_count") val voteCount: Int = 0,
    @Json(name = "genre_ids") val genreIds: List<Int> = emptyList(),
    @Json(name = "genres") val genres: List<GenreDto>? = null
) {
    fun toDomain(): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres?.map { it.toDomain() } ?: emptyList()
    )
}
