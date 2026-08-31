package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.NewsRepository
import com.example.data.repository.RecurringReminderRepository
import com.example.data.repository.StudySessionRepository
import com.example.data.repository.SubjectRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.data.repository.UserProfileRepository
import com.example.notification.NotificationHelper
import com.example.notification.RecurringReminderScheduler
import com.example.notification.TaskReminderScheduler

class StudyFlowApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val userProfileRepository by lazy { UserProfileRepository(database.userProfileDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val subjectRepository by lazy { SubjectRepository(database.subjectDao()) }
    val studySessionRepository by lazy { StudySessionRepository(database.studySessionDao()) }
    val recurringReminderRepository by lazy { RecurringReminderRepository(database.recurringReminderDao()) }
    val newsRepository by lazy { NewsRepository() }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }

    val taskReminderScheduler by lazy { TaskReminderScheduler(this) }
    val recurringReminderScheduler by lazy { RecurringReminderScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
