package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Video>> =
        repository.getMovieVideos(movieId)
}
