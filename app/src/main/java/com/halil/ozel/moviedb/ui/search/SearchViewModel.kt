package com.halil.ozel.moviedb.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Movie
import com.halil.ozel.moviedb.domain.model.PersonSearchResult
import com.halil.ozel.moviedb.domain.model.TvSeries
import com.halil.ozel.moviedb.domain.usecase.SearchMoviesUseCase
import com.halil.ozel.moviedb.domain.usecase.SearchPersonsUseCase
import com.halil.ozel.moviedb.domain.usecase.SearchTvUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchFilter { ALL, MOVIES, TV, PEOPLE }

data class SearchUiState(
    val query: String = "",
    val movies: List<Movie> = emptyList(),
    val tvSeries: List<TvSeries> = emptyList(),
    val persons: List<PersonSearchResult> = emptyList(),
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val searchTvUseCase: SearchTvUseCase,
    private val searchPersonsUseCase: SearchPersonsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query -> performSearch(query) }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            query = query,
            selectedFilter = SearchFilter.ALL
        )
        _queryFlow.value = query
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                movies = emptyList(),
                tvSeries = emptyList(),
                persons = emptyList()
            )
        }
    }

    fun onFilterSelected(filter: SearchFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val moviesResult = searchMoviesUseCase(query)
            val tvResult = searchTvUseCase(query)
            val personsResult = searchPersonsUseCase(query)
            _uiState.value = _uiState.value.copy(
                movies = moviesResult.getOrDefault(emptyList()),
                tvSeries = tvResult.getOrDefault(emptyList()),
                persons = personsResult.getOrDefault(emptyList()),
                isLoading = false
            )
        }
    }
}
