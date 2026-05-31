package com.halil.ozel.moviedb.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import com.halil.ozel.moviedb.domain.repository.TvRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenreListUiState(
    val movies: List<Movie> = emptyList(),
    val tvSeries: List<TvSeries> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val currentPage: Int = 0,
    val error: String? = null
)

@HiltViewModel
class GenreListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val genreId: Int = checkNotNull(savedStateHandle["genreId"])
    val isMovie: Boolean = savedStateHandle.get<String>("mediaType") == "movie"
    val genreName: String = savedStateHandle.get<String>("genreName") ?: ""

    private val _uiState = MutableStateFlow(GenreListUiState())
    val uiState: StateFlow<GenreListUiState> = _uiState.asStateFlow()

    init { loadNextPage() }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return

        val nextPage = state.currentPage + 1
        val isFirst = nextPage == 1

        _uiState.value = state.copy(
            isLoading = isFirst,
            isLoadingMore = !isFirst,
            error = null
        )

        viewModelScope.launch {
            if (isMovie) {
                movieRepository.discoverMoviesByGenre(genreId, nextPage).fold(
                    onSuccess = { items ->
                        _uiState.value = _uiState.value.copy(
                            movies = _uiState.value.movies + items,
                            isLoading = false, isLoadingMore = false,
                            currentPage = nextPage, hasMore = items.size >= 10
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isLoadingMore = false, error = e.message
                        )
                    }
                )
            } else {
                tvRepository.discoverTvByGenre(genreId, nextPage).fold(
                    onSuccess = { items ->
                        _uiState.value = _uiState.value.copy(
                            tvSeries = _uiState.value.tvSeries + items,
                            isLoading = false, isLoadingMore = false,
                            currentPage = nextPage, hasMore = items.size >= 10
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isLoadingMore = false, error = e.message
                        )
                    }
                )
            }
        }
    }
}
