package com.example.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.ColorAccent
import com.example.data.entity.UserProfile
import com.example.data.repository.AppLanguage
import com.example.data.repository.AppThemeMode
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val userProfile: UserProfile? = null
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.userPreferencesFlow,
        userProfileRepository.userProfile
    ) { prefs, profile ->
        SettingsUiState(
            preferences = prefs,
            userProfile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setColorAccent(accent: ColorAccent) {
        viewModelScope.launch {
            preferencesRepository.setColorAccent(accent)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(lang)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setTaskRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setTaskRemindersEnabled(enabled)
        }
    }

    fun setRecurringRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setRecurringRemindersEnabled(enabled)
        }
    }

    fun setDefaultReminderOffset(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setDefaultReminderOffset(minutes)
        }
    }

    fun setPomodoroDurations(focus: Int, shortBreak: Int, longBreak: Int, cycleCount: Int) {
        viewModelScope.launch {
            preferencesRepository.setPomodoroDurations(focus, shortBreak, longBreak, cycleCount)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            val current = uiState.value.userProfile
            if (current != null) {
                userProfileRepository.saveProfile(current.copy(name = name.trim()))
            }
        }
    }

    class Factory(
        private val preferencesRepository: UserPreferencesRepository,
        private val userProfileRepository: UserProfileRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(preferencesRepository, userProfileRepository) as T
        }
    }
}
