package com.example.data.repository

import com.example.data.entity.RecurringReminder
import com.example.data.local.RecurringReminderDao
import kotlinx.coroutines.flow.Flow

class RecurringReminderRepository(private val reminderDao: RecurringReminderDao) {

    fun observeAllReminders(): Flow<List<RecurringReminder>> = reminderDao.observeAllReminders()

    suspend fun getEnabledReminders(): List<RecurringReminder> = reminderDao.getEnabledReminders()

    suspend fun getReminderById(id: Long): RecurringReminder? = reminderDao.getReminderById(id)

    fun observeReminderById(id: Long): Flow<RecurringReminder?> = reminderDao.observeReminderById(id)

    suspend fun insertReminder(reminder: RecurringReminder): Long = reminderDao.insertReminder(reminder)

    suspend fun updateReminder(reminder: RecurringReminder) = reminderDao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: RecurringReminder) = reminderDao.deleteReminder(reminder)

    suspend fun deleteReminderById(id: Long) = reminderDao.deleteReminderById(id)

    suspend fun toggleReminder(id: Long, enabled: Boolean) {
        val reminder = reminderDao.getReminderById(id) ?: return
        reminderDao.updateReminder(reminder.copy(enabled = enabled))
    }

    suspend fun clearAllReminders() = reminderDao.clearAllReminders()
}
