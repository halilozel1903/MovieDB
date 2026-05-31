package com.halil.ozel.moviedb.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.repository.MovieRepository
import com.halil.ozel.moviedb.domain.repository.TvRepository
import com.halil.ozel.moviedb.navigation.MediaCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaListUiState(
    val movies: List<Movie> = emptyList(),
    val tvSeries: List<TvSeries> = emptyList(),
    val isLoading: Boolean = false,   // false so loadNextPage guard passes on init
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true
)

@HiltViewModel
class MediaListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Read the navigation argument directly — no LaunchedEffect needed
    val category: MediaCategory = MediaCategory.valueOf(
        checkNotNull(savedStateHandle["category"]) { "category arg missing" }
    )

    private val _uiState = MutableStateFlow(MediaListUiState())
    val uiState: StateFlow<MediaListUiState> = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

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
            if (category.isMovie) {
                val result = when (category) {
                    MediaCategory.MOVIE_NOW_PLAYING -> movieRepository.getNowPlayingMovies(nextPage)
                    MediaCategory.MOVIE_POPULAR     -> movieRepository.getPopularMovies(nextPage)
                    MediaCategory.MOVIE_TOP_RATED   -> movieRepository.getTopRatedMovies(nextPage)
                    MediaCategory.MOVIE_UPCOMING    -> movieRepository.getUpcomingMovies(nextPage)
                    else                            -> movieRepository.getPopularMovies(nextPage)
                }
                result.fold(
                    onSuccess = { newItems ->
                        _uiState.value = _uiState.value.copy(
                            movies = _uiState.value.movies + newItems,
                            isLoading = false, isLoadingMore = false,
                            currentPage = nextPage,
                            hasMore = newItems.size >= 10
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isLoadingMore = false,
                            error = e.message
                        )
                    }
                )
            } else {
                val result = when (category) {
                    MediaCategory.TV_POPULAR     -> tvRepository.getPopularTv(nextPage)
                    MediaCategory.TV_TOP_RATED   -> tvRepository.getTopRatedTv(nextPage)
                    MediaCategory.TV_AIRING_TODAY -> tvRepository.getAiringTodayTv(nextPage)
                    MediaCategory.TV_ON_THE_AIR  -> tvRepository.getOnTheAirTv(nextPage)
                    else                         -> tvRepository.getPopularTv(nextPage)
                }
                result.fold(
                    onSuccess = { newItems ->
                        _uiState.value = _uiState.value.copy(
                            tvSeries = _uiState.value.tvSeries + newItems,
                            isLoading = false, isLoadingMore = false,
                            currentPage = nextPage,
                            hasMore = newItems.size >= 10
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, isLoadingMore = false,
                            error = e.message
                        )
                    }
                )
            }
        }
    }
}
