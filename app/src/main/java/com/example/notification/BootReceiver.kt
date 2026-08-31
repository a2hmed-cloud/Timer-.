package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val taskScheduler = TaskReminderScheduler(context)
                    val recurringScheduler = RecurringReminderScheduler(context)

                    // 1. Reschedule all active tasks with reminders
                    val activeTasks = db.taskDao().getActiveTasksWithReminders()
                    activeTasks.forEach { task ->
                        taskScheduler.scheduleTaskReminder(task)
                    }

                    // 2. Reschedule all enabled recurring reminders
                    val enabledReminders = db.recurringReminderDao().getEnabledReminders()
                    enabledReminders.forEach { reminder ->
                        recurringScheduler.scheduleRecurringReminder(reminder)
                    }
                } catch (e: Exception) {
                    // Safe error capture
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
