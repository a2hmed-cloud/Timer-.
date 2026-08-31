package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.RecurringReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringReminderDao {
    @Query("SELECT * FROM recurring_reminders ORDER BY timeOfDayMinutes ASC")
    fun observeAllReminders(): Flow<List<RecurringReminder>>

    @Query("SELECT * FROM recurring_reminders WHERE enabled = 1")
    suspend fun getEnabledReminders(): List<RecurringReminder>

    @Query("SELECT * FROM recurring_reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): RecurringReminder?

    @Query("SELECT * FROM recurring_reminders WHERE id = :id")
    fun observeReminderById(id: Long): Flow<RecurringReminder?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: RecurringReminder): Long

    @Update
    suspend fun updateReminder(reminder: RecurringReminder)

    @Delete
    suspend fun deleteReminder(reminder: RecurringReminder)

    @Query("DELETE FROM recurring_reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("DELETE FROM recurring_reminders")
    suspend fun clearAllReminders()
}
