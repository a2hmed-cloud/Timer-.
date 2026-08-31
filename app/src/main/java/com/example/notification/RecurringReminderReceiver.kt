package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.entity.RepeatType
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecurringReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("EXTRA_REMINDER_ID", -1L)
        val title = intent.getStringExtra("EXTRA_REMINDER_TITLE") ?: return
        val msg = intent.getStringExtra("EXTRA_REMINDER_MSG")

        if (reminderId <= 0) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val reminder = db.recurringReminderDao().getReminderById(reminderId)
                if (reminder != null && reminder.enabled) {
                    NotificationHelper.showRecurringReminderNotification(
                        context = context,
                        reminderId = reminderId,
                        title = title,
                        message = msg
                    )

                    // Reschedule next occurrence if repeating
                    if (reminder.repeatType != RepeatType.ONCE) {
                        val scheduler = RecurringReminderScheduler(context)
                        scheduler.scheduleRecurringReminder(reminder)
                    } else {
                        // Once completed, disable it
                        db.recurringReminderDao().updateReminder(reminder.copy(enabled = false))
                    }
                }
            } catch (e: Exception) {
                // Ignore or log error
            } finally {
                pendingResult.finish()
            }
        }
    }
}
