package com.halil.ozel.moviedb.ui.detail.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.Episode
import com.halil.ozel.moviedb.domain.model.ExternalRatings
import com.halil.ozel.moviedb.domain.model.TvSeries
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

data class TvDetailUiState(
    val tvSeries: TvSeries? = null,
    val cast: List<Cast> = emptyList(),
    val recommended: List<TvSeries> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedSeasonNumber: Int? = null,
    val seasonEpisodes: Map<Int, List<Episode>> = emptyMap(),
    val episodesLoading: Boolean = false,
    val episodesError: Map<Int, Boolean> = emptyMap(),
    val trailer: Video? = null,
    val watchProviders: List<WatchProvider> = emptyList(),
    val imdbId: String? = null,
    val externalRatings: ExternalRatings? = null
)

@HiltViewModel
class TvDetailViewModel @Inject constructor(
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val getTvCastUseCase: GetTvCastUseCase,
    private val getRecommendedTvUseCase: GetRecommendedTvUseCase,
    private val isTvFavoriteUseCase: IsTvFavoriteUseCase,
    private val toggleTvFavoriteUseCase: ToggleTvFavoriteUseCase,
    private val getSeasonDetailsUseCase: GetSeasonDetailsUseCase,
    private val getTvVideosUseCase: GetTvVideosUseCase,
    private val getTvWatchProvidersUseCase: GetTvWatchProvidersUseCase,
    private val tvRepository: com.halil.ozel.moviedb.domain.repository.TvRepository,
    private val omdbRepository: OmdbRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvDetailUiState(isLoading = true))
    val uiState: StateFlow<TvDetailUiState> = _uiState.asStateFlow()

    fun load(tvId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val tvResult = getTvDetailUseCase(tvId)
            val castResult = getTvCastUseCase(tvId)
            val recommendedResult = getRecommendedTvUseCase(tvId)
            val videosResult = getTvVideosUseCase(tvId)
            val watchProvidersResult = getTvWatchProvidersUseCase(tvId)

            val trailer = videosResult.getOrDefault(emptyList())
                .filter { it.site == "YouTube" }
                .let { videos ->
                    videos.firstOrNull { it.type == "Trailer" && it.official }
                        ?: videos.firstOrNull { it.type == "Trailer" }
                        ?: videos.firstOrNull()
                }

            val externalImdbId = tvRepository.getTvExternalIds(tvId).getOrNull()
            val externalRatings = externalImdbId?.let { omdbRepository.getRatings(it).getOrNull() }

            _uiState.value = _uiState.value.copy(
                tvSeries = tvResult.getOrNull(),
                cast = castResult.getOrDefault(emptyList()).take(20),
                recommended = recommendedResult.getOrDefault(emptyList()),
                isLoading = false,
                error = tvResult.exceptionOrNull()?.message,
                trailer = trailer,
                watchProviders = watchProvidersResult.getOrDefault(emptyList()),
                imdbId = externalImdbId,
                externalRatings = externalRatings
            )
        }
        viewModelScope.launch {
            isTvFavoriteUseCase(tvId).collect { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    fun toggleFavorite() {
        val tv = _uiState.value.tvSeries ?: return
        viewModelScope.launch { toggleTvFavoriteUseCase(tv) }
    }

    fun toggleSeasonExpanded(seasonNumber: Int) {
        val current = _uiState.value.expandedSeasonNumber
        if (current == seasonNumber) {
            _uiState.value = _uiState.value.copy(expandedSeasonNumber = null)
            return
        }
        _uiState.value = _uiState.value.copy(expandedSeasonNumber = seasonNumber)
        // Load if not already cached or if previous attempt errored
        val hasError = _uiState.value.episodesError[seasonNumber] == true
        if (!_uiState.value.seasonEpisodes.containsKey(seasonNumber) || hasError) {
            loadEpisodes(seasonNumber)
        }
    }

    fun retryEpisodes(seasonNumber: Int) = loadEpisodes(seasonNumber)

    private fun loadEpisodes(seasonNumber: Int) {
        val tvId = _uiState.value.tvSeries?.id ?: return
        viewModelScope.launch {
            // Clear error flag and start loading
            val errorsCleared = _uiState.value.episodesError.toMutableMap().also { it.remove(seasonNumber) }
            _uiState.value = _uiState.value.copy(episodesLoading = true, episodesError = errorsCleared)
            val result = getSeasonDetailsUseCase(tvId, seasonNumber)
            val season = result.getOrNull()
            if (season != null) {
                val updated = _uiState.value.seasonEpisodes.toMutableMap()
                updated[seasonNumber] = season.episodes
                _uiState.value = _uiState.value.copy(
                    seasonEpisodes = updated,
                    episodesLoading = false
                )
            } else {
                val errors = _uiState.value.episodesError.toMutableMap().also { it[seasonNumber] = true }
                _uiState.value = _uiState.value.copy(episodesLoading = false, episodesError = errors)
            }
        }
    }
}
