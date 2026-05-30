package com.halil.ozel.moviedb.ui.detail.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.Cast
import com.halil.ozel.moviedb.domain.model.TvSeries
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
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TvDetailViewModel @Inject constructor(
    private val getTvDetailUseCase: GetTvDetailUseCase,
    private val getTvCastUseCase: GetTvCastUseCase,
    private val getRecommendedTvUseCase: GetRecommendedTvUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TvDetailUiState(isLoading = true))
    val uiState: StateFlow<TvDetailUiState> = _uiState.asStateFlow()

    fun load(tvId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val tvResult = getTvDetailUseCase(tvId)
            val castResult = getTvCastUseCase(tvId)
            val recommendedResult = getRecommendedTvUseCase(tvId)

            _uiState.value = _uiState.value.copy(
                tvSeries = tvResult.getOrNull(),
                cast = castResult.getOrDefault(emptyList()).take(20),
                recommended = recommendedResult.getOrDefault(emptyList()),
                isLoading = false,
                error = tvResult.exceptionOrNull()?.message
            )
        }
    }
}
