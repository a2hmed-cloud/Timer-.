package com.example.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.catalog.EducationCatalog
import com.example.data.entity.ColorAccent
import com.example.data.entity.Subject
import com.example.data.entity.UserProfile
import com.example.data.repository.AppThemeMode
import com.example.data.repository.SubjectRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.UserProfileRepository
import com.example.domain.model.CatalogSubject
import com.example.domain.model.EducationCountry
import com.example.domain.model.EducationGrade
import com.example.domain.model.EducationSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 1, // 1 to 10
    val totalSteps: Int = 10,
    val name: String = "",
    val availableCountries: List<EducationCountry> = EducationCatalog.countries,
    val selectedCountry: EducationCountry? = null,
    val selectedSystem: EducationSystem? = null,
    val selectedGrade: EducationGrade? = null,
    val selectedCatalogSubjects: List<CatalogSubject> = emptyList(),
    val customSubjects: List<Subject> = emptyList(),
    val selectedThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val selectedColorAccent: ColorAccent = ColorAccent.DYNAMIC,
    val notificationsAllowed: Boolean = true,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val isSaving: Boolean = false
)

class OnboardingViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val subjectRepository: SubjectRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun nextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun selectCountry(country: EducationCountry) {
        _uiState.update {
            it.copy(
                selectedCountry = country,
                selectedSystem = country.systems.firstOrNull(),
                selectedGrade = country.systems.firstOrNull()?.grades?.firstOrNull()
            )
        }
        updateCatalogSubjects()
    }

    fun selectSystem(system: EducationSystem) {
        _uiState.update {
            it.copy(
                selectedSystem = system,
                selectedGrade = system.grades.firstOrNull()
            )
        }
        updateCatalogSubjects()
    }

    fun selectGrade(grade: EducationGrade) {
        _uiState.update { it.copy(selectedGrade = grade) }
        updateCatalogSubjects()
    }

    private fun updateCatalogSubjects() {
        val grade = _uiState.value.selectedGrade
        val subjects = grade?.subjects ?: emptyList()
        _uiState.update { it.copy(selectedCatalogSubjects = subjects) }
    }

    fun toggleCatalogSubject(subject: CatalogSubject) {
        val current = _uiState.value.selectedCatalogSubjects
        val updated = if (current.any { it.id == subject.id }) {
            current.filter { it.id != subject.id }
        } else {
            current + subject
        }
        _uiState.update { it.copy(selectedCatalogSubjects = updated) }
    }

    fun addCustomSubject(name: String, colorHex: Long = 0xFF4338CA) {
        if (name.isBlank()) return
        val newSub = Subject(
            name = name.trim(),
            color = colorHex,
            sortOrder = _uiState.value.customSubjects.size + _uiState.value.selectedCatalogSubjects.size
        )
        _uiState.update { it.copy(customSubjects = it.customSubjects + newSub) }
    }

    fun removeCustomSubject(subject: Subject) {
        _uiState.update { it.copy(customSubjects = it.customSubjects.filter { s -> s != subject }) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _uiState.update { it.copy(selectedThemeMode = mode) }
    }

    fun setColorAccent(accent: ColorAccent) {
        _uiState.update { it.copy(selectedColorAccent = accent) }
    }

    fun setNotificationsAllowed(allowed: Boolean) {
        _uiState.update { it.copy(notificationsAllowed = allowed) }
    }

    fun setPomodoroDurations(focus: Int, shortBreak: Int, longBreak: Int, cycleCount: Int) {
        _uiState.update {
            it.copy(
                focusDurationMinutes = focus,
                shortBreakMinutes = shortBreak,
                longBreakMinutes = longBreak,
                sessionsBeforeLongBreak = cycleCount
            )
        }
    }

    fun finishOnboarding(onSuccess: (startWithNewTask: Boolean) -> Unit, startWithNewTask: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value

            // 1. Save User Profile
            val profile = UserProfile(
                id = 1,
                name = state.name.trim().ifBlank { "Student" },
                educationCountryId = state.selectedCountry?.id,
                educationCountryName = state.selectedCountry?.nameAr ?: state.selectedCountry?.nameEn,
                educationSystemId = state.selectedSystem?.id,
                educationSystemName = state.selectedSystem?.nameAr ?: state.selectedSystem?.nameEn,
                gradeId = state.selectedGrade?.id,
                gradeName = state.selectedGrade?.nameAr ?: state.selectedGrade?.nameEn,
                onboardingCompleted = true,
                createdAt = System.currentTimeMillis()
            )
            userProfileRepository.saveProfile(profile)

            // 2. Save Subjects to Room
            val subjectsToSave = mutableListOf<Subject>()
            var sort = 0
            state.selectedCatalogSubjects.forEach { catSub ->
                subjectsToSave.add(
                    Subject(
                        name = catSub.nameAr.ifBlank { catSub.nameEn },
                        catalogSubjectId = catSub.id,
                        color = catSub.colorHex,
                        sortOrder = sort++,
                        isActive = true
                    )
                )
            }
            state.customSubjects.forEach { customSub ->
                subjectsToSave.add(
                    customSub.copy(
                        sortOrder = sort++,
                        isActive = true
                    )
                )
            }

            if (subjectsToSave.isNotEmpty()) {
                subjectRepository.insertSubjects(subjectsToSave)
            }

            // 3. Save Preferences to DataStore
            preferencesRepository.setThemeMode(state.selectedThemeMode)
            preferencesRepository.setColorAccent(state.selectedColorAccent)
            preferencesRepository.setNotificationsEnabled(state.notificationsAllowed)
            preferencesRepository.setPomodoroDurations(
                focus = state.focusDurationMinutes,
                shortBreak = state.shortBreakMinutes,
                longBreak = state.longBreakMinutes,
                cycleCount = state.sessionsBeforeLongBreak
            )
            preferencesRepository.setOnboardingCompleted(true)

            _uiState.update { it.copy(isSaving = false) }
            onSuccess(startWithNewTask)
        }
    }

    class Factory(
        private val userProfileRepository: UserProfileRepository,
        private val subjectRepository: SubjectRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(
                userProfileRepository,
                subjectRepository,
                preferencesRepository
            ) as T
        }
    }
}
