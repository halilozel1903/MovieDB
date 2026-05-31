package com.halil.ozel.moviedb.ui.main

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val onboardingKey = booleanPreferencesKey("onboarding_complete")

    /**
     * null  = DataStore henüz okunmadı (anlık splash)
     * false = ilk kurulum, onboarding gösterilmeli
     * true  = onboarding tamamlandı
     */
    val isOnboardingComplete: StateFlow<Boolean?> = dataStore.data
        .map { prefs -> prefs[onboardingKey] ?: false }   // key yoksa → false (ilk kurulum)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.edit { it[onboardingKey] = true }
        }
    }
}
