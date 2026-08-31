package com.example.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.RecurringReminder
import com.example.data.entity.RepeatType
import com.example.data.repository.RecurringReminderRepository
import com.example.domain.planner.RecurringScheduleCalculator
import com.example.notification.RecurringReminderScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class AddEditReminderUiState(
    val reminderId: Long? = null,
    val title: String = "",
    val message: String = "",
    val repeatType: RepeatType = RepeatType.DAILY,
    val hour: Int = 19, // 7 PM
    val minute: Int = 0,
    val selectedDaysOfWeek: Set<Int> = setOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY), // Calendar constants
    val dayOfMonth: Int = 1,
    val titleError: String? = null,
    val isSaving: Boolean = false
)

sealed interface AddEditReminderEvent {
    data object SaveSuccess : AddEditReminderEvent
    data class ShowError(val message: String) : AddEditReminderEvent
}

class AddEditReminderViewModel(
    private val reminderId: Long?,
    private val reminderRepository: RecurringReminderRepository,
    private val recurringReminderScheduler: RecurringReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditReminderUiState(reminderId = reminderId))
    val uiState: StateFlow<AddEditReminderUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditReminderEvent>()
    val events: SharedFlow<AddEditReminderEvent> = _events.asSharedFlow()

    init {
        if (reminderId != null && reminderId > 0) {
            loadReminder()
        }
    }

    private fun loadReminder() {
        viewModelScope.launch {
            val reminder = reminderRepository.getReminderById(reminderId ?: return@launch)
            if (reminder != null) {
                val hour = reminder.timeOfDayMinutes / 60
                val minute = reminder.timeOfDayMinutes % 60
                val parsedDays = RecurringScheduleCalculator.parseDaysOfWeek(reminder.daysOfWeek)
                _uiState.update {
                    it.copy(
                        title = reminder.title,
                        message = reminder.message ?: "",
                        repeatType = reminder.repeatType,
                        hour = hour,
                        minute = minute,
                        selectedDaysOfWeek = parsedDays,
                        dayOfMonth = reminder.dayOfMonth ?: 1
                    )
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    fun onMessageChange(newMsg: String) {
        _uiState.update { it.copy(message = newMsg) }
    }

    fun onRepeatTypeChange(type: RepeatType) {
        _uiState.update { it.copy(repeatType = type) }
    }

    fun onTimeChange(hour: Int, minute: Int) {
        _uiState.update { it.copy(hour = hour, minute = minute) }
    }

    fun toggleDayOfWeek(calendarDay: Int) {
        _uiState.update { state ->
            val current = state.selectedDaysOfWeek.toMutableSet()
            if (current.contains(calendarDay)) {
                current.remove(calendarDay)
            } else {
                current.add(calendarDay)
            }
            state.copy(selectedDaysOfWeek = current)
        }
    }

    fun onDayOfMonthChange(day: Int) {
        _uiState.update { it.copy(dayOfMonth = day.coerceIn(1, 31)) }
    }

    fun saveReminder() {
        val state = _uiState.value
        val titleTrimmed = state.title.trim()

        if (titleTrimmed.isEmpty()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        val timeMinutes = (state.hour * 60) + state.minute
        val daysString = if (state.repeatType == RepeatType.CUSTOM || state.repeatType == RepeatType.WEEKLY) {
            RecurringScheduleCalculator.formatIsoDays(state.selectedDaysOfWeek)
        } else {
            null
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val reminder = RecurringReminder(
                    id = state.reminderId ?: 0L,
                    title = titleTrimmed,
                    message = state.message.trim().ifEmpty { null },
                    repeatType = state.repeatType,
                    timeOfDayMinutes = timeMinutes,
                    daysOfWeek = daysString,
                    dayOfMonth = if (state.repeatType == RepeatType.MONTHLY) state.dayOfMonth else null,
                    enabled = true,
                    createdAt = System.currentTimeMillis()
                )

                if (reminder.id > 0) {
                    reminderRepository.updateReminder(reminder)
                    recurringReminderScheduler.cancelRecurringReminder(reminder.id)
                    recurringReminderScheduler.scheduleRecurringReminder(reminder)
                } else {
                    val newId = reminderRepository.insertReminder(reminder)
                    recurringReminderScheduler.scheduleRecurringReminder(reminder.copy(id = newId))
                }

                _events.emit(AddEditReminderEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(AddEditReminderEvent.ShowError(e.message ?: "Failed to save reminder"))
            }
        }
    }

    class Factory(
        private val reminderId: Long?,
        private val reminderRepository: RecurringReminderRepository,
        private val recurringReminderScheduler: RecurringReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditReminderViewModel(reminderId, reminderRepository, recurringReminderScheduler) as T
        }
    }
}
