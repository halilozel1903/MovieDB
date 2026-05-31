package com.halil.ozel.moviedb.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.usecase.GetFavoritesUseCase
import com.halil.ozel.moviedb.domain.usecase.GetTvFavoritesUseCase
import com.halil.ozel.moviedb.domain.usecase.ToggleFavoriteUseCase
import com.halil.ozel.moviedb.domain.usecase.ToggleTvFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favorites: List<Movie> = emptyList(),
    val tvFavorites: List<TvSeries> = emptyList(),
    val isLoading: Boolean = true,
    val toastMessage: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getTvFavoritesUseCase: GetTvFavoritesUseCase,
    private val toggleTvFavoriteUseCase: ToggleTvFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    private val _tvFavorites = MutableStateFlow<List<TvSeries>>(emptyList())

    init {
        viewModelScope.launch {
            getFavoritesUseCase().collect { movies ->
                _favorites.value = movies
                _uiState.value = _uiState.value.copy(favorites = movies, isLoading = false)
            }
        }
        viewModelScope.launch {
            getTvFavoritesUseCase().collect { tvList ->
                _tvFavorites.value = tvList
                _uiState.value = _uiState.value.copy(tvFavorites = tvList)
            }
        }
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
            val isFavorite = _favorites.value.any { it.id == movie.id }
            _uiState.value = _uiState.value.copy(
                toastMessage = if (isFavorite) "Removed from favorites" else "Added to favorites"
            )
        }
    }

    fun toggleTvFavorite(tvSeries: TvSeries) {
        viewModelScope.launch {
            toggleTvFavoriteUseCase(tvSeries)
            val isFavorite = _tvFavorites.value.any { it.id == tvSeries.id }
            _uiState.value = _uiState.value.copy(
                toastMessage = if (isFavorite) "Removed from favorites" else "Added to favorites"
            )
        }
    }

    fun clearToastMessage() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }
}
