package com.example.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.RecurringReminder
import com.example.data.repository.RecurringReminderRepository
import com.example.notification.RecurringReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemindersViewModel(
    private val reminderRepository: RecurringReminderRepository,
    private val recurringReminderScheduler: RecurringReminderScheduler
) : ViewModel() {

    val reminders: StateFlow<List<RecurringReminder>> = reminderRepository.observeAllReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleReminder(reminder: RecurringReminder) {
        val newStatus = !reminder.enabled
        viewModelScope.launch {
            reminderRepository.toggleReminder(reminder.id, newStatus)
            if (newStatus) {
                recurringReminderScheduler.scheduleRecurringReminder(reminder.copy(enabled = true))
            } else {
                recurringReminderScheduler.cancelRecurringReminder(reminder.id)
            }
        }
    }

    fun deleteReminder(reminder: RecurringReminder) {
        viewModelScope.launch {
            recurringReminderScheduler.cancelRecurringReminder(reminder.id)
            reminderRepository.deleteReminder(reminder)
        }
    }

    class Factory(
        private val reminderRepository: RecurringReminderRepository,
        private val recurringReminderScheduler: RecurringReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RemindersViewModel(reminderRepository, recurringReminderScheduler) as T
        }
    }
}
