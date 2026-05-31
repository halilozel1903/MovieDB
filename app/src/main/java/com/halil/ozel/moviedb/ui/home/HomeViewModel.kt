package com.halil.ozel.moviedb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Genre
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val popularMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val popularTv: List<TvSeries> = emptyList(),
    val topRatedTv: List<TvSeries> = emptyList(),
    val airingTodayTv: List<TvSeries> = emptyList(),
    val onTheAirTv: List<TvSeries> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toastMessage: String? = null,
    val genres: List<Genre> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getPopularTvUseCase: GetPopularTvUseCase,
    private val getTopRatedTvUseCase: GetTopRatedTvUseCase,
    private val getAiringTodayTvUseCase: GetAiringTodayTvUseCase,
    private val getOnTheAirTvUseCase: GetOnTheAirTvUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleTvFavoriteUseCase: ToggleTvFavoriteUseCase,
    private val getTvFavoritesUseCase: GetTvFavoritesUseCase,
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getTvGenresUseCase: GetTvGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _tvFavoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val tvFavoriteIds: StateFlow<Set<Int>> = _tvFavoriteIds.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadAll()
        observeFavorites()
        observeTvFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { _favoriteIds.value = it.map { m -> m.id }.toSet() }
        }
    }

    private fun observeTvFavorites() {
        viewModelScope.launch {
            getTvFavoritesUseCase().collect { _tvFavoriteIds.value = it.map { t -> t.id }.toSet() }
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val genres = getMovieGenresUseCase().getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(
                popularMovies = getPopularMoviesUseCase().getOrDefault(emptyList()),
                nowPlayingMovies = getNowPlayingMoviesUseCase().getOrDefault(emptyList()),
                topRatedMovies = getTopRatedMoviesUseCase().getOrDefault(emptyList()),
                upcomingMovies = getUpcomingMoviesUseCase().getOrDefault(emptyList()),
                popularTv = getPopularTvUseCase().getOrDefault(emptyList()),
                topRatedTv = getTopRatedTvUseCase().getOrDefault(emptyList()),
                airingTodayTv = getAiringTodayTvUseCase().getOrDefault(emptyList()),
                onTheAirTv = getOnTheAirTvUseCase().getOrDefault(emptyList()),
                genres = genres,
                isLoading = false
            )
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
            val isFavorite = favoriteIds.value.contains(movie.id)
            _toastMessage.value = if (isFavorite) "Removed from favorites" else "Added to favorites"
        }
    }

    fun toggleTvFavorite(tvSeries: TvSeries) {
        viewModelScope.launch {
            toggleTvFavoriteUseCase(tvSeries)
            val isFavorite = tvFavoriteIds.value.contains(tvSeries.id)
            _toastMessage.value = if (isFavorite) "Removed from favorites" else "Added to favorites"
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
