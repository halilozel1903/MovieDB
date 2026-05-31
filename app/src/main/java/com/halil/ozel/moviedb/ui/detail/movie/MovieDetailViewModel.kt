package com.halil.ozel.moviedb.ui.detail.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.ExternalRatings
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.Video
import com.halil.ozel.moviedb.domain.model.WatchProvider
import com.halil.ozel.moviedb.domain.repository.OmdbRepository
import com.halil.ozel.moviedb.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val movie: Movie? = null,
    val cast: List<Cast> = emptyList(),
    val recommended: List<Movie> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val trailer: Video? = null,
    val watchProviders: List<WatchProvider> = emptyList(),
    val externalRatings: ExternalRatings? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieCastUseCase: GetMovieCastUseCase,
    private val getRecommendedMoviesUseCase: GetRecommendedMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val getMovieWatchProvidersUseCase: GetMovieWatchProvidersUseCase,
    private val omdbRepository: OmdbRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState(isLoading = true))
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    fun load(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val movieResult = getMovieDetailUseCase(movieId)
            val castResult = getMovieCastUseCase(movieId)
            val recommendedResult = getRecommendedMoviesUseCase(movieId)
            val videosResult = getMovieVideosUseCase(movieId)
            val watchProvidersResult = getMovieWatchProvidersUseCase(movieId)

            val trailer = videosResult.getOrDefault(emptyList())
                .filter { it.site == "YouTube" }
                .let { videos ->
                    videos.firstOrNull { it.type == "Trailer" && it.official }
                        ?: videos.firstOrNull { it.type == "Trailer" }
                        ?: videos.firstOrNull()
                }

            val movie = movieResult.getOrNull()
            val externalRatings = movie?.imdbId?.let { imdbId ->
                omdbRepository.getRatings(imdbId).getOrNull()
            }

            _uiState.value = _uiState.value.copy(
                movie = movie,
                cast = castResult.getOrDefault(emptyList()).take(20),
                recommended = recommendedResult.getOrDefault(emptyList()),
                isLoading = false,
                error = movieResult.exceptionOrNull()?.message,
                trailer = trailer,
                watchProviders = watchProvidersResult.getOrDefault(emptyList()),
                externalRatings = externalRatings
            )
        }
        viewModelScope.launch {
            isFavoriteUseCase(movieId).collect { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    fun toggleFavorite() {
        val movie = _uiState.value.movie ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
        }
    }
}
