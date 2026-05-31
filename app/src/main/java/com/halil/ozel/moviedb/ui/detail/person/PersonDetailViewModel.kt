package com.halil.ozel.moviedb.ui.detail.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halil.ozel.moviedb.domain.model.PersonCredit
import com.halil.ozel.moviedb.domain.model.PersonInfo
import com.halil.ozel.moviedb.domain.usecase.GetCombinedCreditsUseCase
import com.halil.ozel.moviedb.domain.usecase.GetPersonInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonDetailUiState(
    val credits: List<PersonCredit> = emptyList(),
    val personInfo: PersonInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val getCombinedCreditsUseCase: GetCombinedCreditsUseCase,
    private val getPersonInfoUseCase: GetPersonInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonDetailUiState(isLoading = true))
    val uiState: StateFlow<PersonDetailUiState> = _uiState.asStateFlow()

    fun load(personId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val creditsResult = getCombinedCreditsUseCase(personId)
            val infoResult = getPersonInfoUseCase(personId)
            _uiState.value = _uiState.value.copy(
                credits = creditsResult.getOrDefault(emptyList()),
                personInfo = infoResult.getOrNull(),
                isLoading = false,
                error = creditsResult.exceptionOrNull()?.message
            )
        }
    }
}
