package com.halil.ozel.moviedb.domain.usecase

import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.repository.TvRepository
import javax.inject.Inject

class GetTvVideosUseCase @Inject constructor(
    private val repository: TvRepository
) {
    suspend operator fun invoke(tvId: Int): Result<List<Video>> =
        repository.getTvVideos(tvId)
}
