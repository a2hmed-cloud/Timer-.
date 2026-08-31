package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus

class TaskReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleTaskReminder(task: Task) {
        // Do not schedule for completed tasks or if no reminder offset
        if (task.status == TaskStatus.COMPLETED) {
            cancelTaskReminder(task.id)
            return
        }

        val targetTime = task.dueAt ?: task.scheduledAt
        val offsetMinutes = task.reminderOffsetMinutes

        if (targetTime == null || offsetMinutes == null) {
            cancelTaskReminder(task.id)
            return
        }

        val triggerTime = targetTime - (offsetMinutes * 60 * 1000L)
        if (triggerTime <= System.currentTimeMillis()) {
            // Already passed
            return
        }

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("EXTRA_TASK_ID", task.id)
            putExtra("EXTRA_TASK_TITLE", task.title)
            putExtra("EXTRA_TASK_DESC", task.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ("task_reminder_${task.id}").hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for devices restricting exact alarms
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelTaskReminder(taskId: Long) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ("task_reminder_$taskId").hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
