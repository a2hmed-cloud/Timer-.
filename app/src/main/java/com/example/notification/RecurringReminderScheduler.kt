package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.entity.RecurringReminder
import com.example.domain.planner.RecurringScheduleCalculator

class RecurringReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleRecurringReminder(reminder: RecurringReminder) {
        if (!reminder.enabled) {
            cancelRecurringReminder(reminder.id)
            return
        }

        val triggerTime = RecurringScheduleCalculator.calculateNextTriggerTime(reminder)

        val intent = Intent(context, RecurringReminderReceiver::class.java).apply {
            putExtra("EXTRA_REMINDER_ID", reminder.id)
            putExtra("EXTRA_REMINDER_TITLE", reminder.title)
            putExtra("EXTRA_REMINDER_MSG", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ("recurring_alarm_${reminder.id}").hashCode(),
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
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelRecurringReminder(reminderId: Long) {
        val intent = Intent(context, RecurringReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ("recurring_alarm_$reminderId").hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
