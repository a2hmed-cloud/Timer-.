package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.entity.ColorAccent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "studyflow_user_prefs")

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage {
    SYSTEM,
    ENGLISH,
    ARABIC
}

data class UserPreferences(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val colorAccent: ColorAccent = ColorAccent.DYNAMIC,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val taskRemindersEnabled: Boolean = true,
    val recurringRemindersEnabled: Boolean = true,
    val defaultReminderOffsetMinutes: Int = 15,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val onboardingCompleted: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val COLOR_ACCENT = stringPreferencesKey("color_accent")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val TASK_REMINDERS_ENABLED = booleanPreferencesKey("task_reminders_enabled")
        val RECURRING_REMINDERS_ENABLED = booleanPreferencesKey("recurring_reminders_enabled")
        val DEFAULT_REMINDER_OFFSET = intPreferencesKey("default_reminder_offset")
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK = intPreferencesKey("short_break")
        val LONG_BREAK = intPreferencesKey("long_break")
        val SESSIONS_BEFORE_LONG_BREAK = intPreferencesKey("sessions_before_long_break")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val themeStr = preferences[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val accentStr = preferences[PreferencesKeys.COLOR_ACCENT] ?: ColorAccent.DYNAMIC.name
        val langStr = preferences[PreferencesKeys.LANGUAGE] ?: AppLanguage.SYSTEM.name
        val notifs = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        val taskReminders = preferences[PreferencesKeys.TASK_REMINDERS_ENABLED] ?: true
        val recReminders = preferences[PreferencesKeys.RECURRING_REMINDERS_ENABLED] ?: true
        val defaultOffset = preferences[PreferencesKeys.DEFAULT_REMINDER_OFFSET] ?: 15
        val focusDur = preferences[PreferencesKeys.FOCUS_DURATION] ?: 25
        val shortBreak = preferences[PreferencesKeys.SHORT_BREAK] ?: 5
        val longBreak = preferences[PreferencesKeys.LONG_BREAK] ?: 15
        val sessionsBeforeLong = preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] ?: 4
        val onboarding = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false

        UserPreferences(
            themeMode = try { AppThemeMode.valueOf(themeStr) } catch (e: Exception) { AppThemeMode.SYSTEM },
            colorAccent = try { ColorAccent.valueOf(accentStr) } catch (e: Exception) { ColorAccent.DYNAMIC },
            language = try { AppLanguage.valueOf(langStr) } catch (e: Exception) { AppLanguage.SYSTEM },
            notificationsEnabled = notifs,
            taskRemindersEnabled = taskReminders,
            recurringRemindersEnabled = recReminders,
            defaultReminderOffsetMinutes = defaultOffset,
            focusDurationMinutes = focusDur,
            shortBreakMinutes = shortBreak,
            longBreakMinutes = longBreak,
            sessionsBeforeLongBreak = sessionsBeforeLong,
            onboardingCompleted = onboarding
        )
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setColorAccent(accent: ColorAccent) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.COLOR_ACCENT] = accent.name
        }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = lang.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setTaskRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TASK_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setRecurringRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RECURRING_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setDefaultReminderOffset(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REMINDER_OFFSET] = minutes
        }
    }

    suspend fun setPomodoroDurations(focus: Int, shortBreak: Int, longBreak: Int, cycleCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOCUS_DURATION] = focus
            preferences[PreferencesKeys.SHORT_BREAK] = shortBreak
            preferences[PreferencesKeys.LONG_BREAK] = longBreak
            preferences[PreferencesKeys.SESSIONS_BEFORE_LONG_BREAK] = cycleCount
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun resetAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
