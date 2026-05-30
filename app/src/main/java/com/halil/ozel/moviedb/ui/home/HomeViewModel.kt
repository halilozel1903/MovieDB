package com.halil.ozel.moviedb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = false,
    val error: String? = null
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
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    init {
        loadAll()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favorites ->
                _favoriteIds.value = favorites.map { it.id }.toSet()
            }
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val popularResult = getPopularMoviesUseCase()
            val nowPlayingResult = getNowPlayingMoviesUseCase()
            val topRatedResult = getTopRatedMoviesUseCase()
            val upcomingResult = getUpcomingMoviesUseCase()
            val popularTvResult = getPopularTvUseCase()
            val topRatedTvResult = getTopRatedTvUseCase()
            val airingTodayTvResult = getAiringTodayTvUseCase()

            _uiState.value = _uiState.value.copy(
                popularMovies = popularResult.getOrDefault(emptyList()),
                nowPlayingMovies = nowPlayingResult.getOrDefault(emptyList()),
                topRatedMovies = topRatedResult.getOrDefault(emptyList()),
                upcomingMovies = upcomingResult.getOrDefault(emptyList()),
                popularTv = popularTvResult.getOrDefault(emptyList()),
                topRatedTv = topRatedTvResult.getOrDefault(emptyList()),
                airingTodayTv = airingTodayTvResult.getOrDefault(emptyList()),
                isLoading = false
            )
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
        }
    }
}
