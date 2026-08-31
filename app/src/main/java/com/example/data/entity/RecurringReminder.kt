package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_reminders")
data class RecurringReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long = 1,

    val title: String,

    val message: String? = null,

    val repeatType: RepeatType = RepeatType.DAILY,

    val timeOfDayMinutes: Int, // Minutes from midnight (e.g. 19 * 60 = 1140 for 7:00 PM)

    val daysOfWeek: String? = null, // Comma-separated ISO day numbers: 1=Mon, 2=Tue, ..., 7=Sun

    val dayOfMonth: Int? = null, // 1..31

    val enabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis()
)
