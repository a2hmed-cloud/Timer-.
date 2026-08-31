package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_TASK_REMINDERS = "channel_task_reminders"
    const val CHANNEL_RECURRING_REMINDERS = "channel_recurring_reminders"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val taskChannel = NotificationChannel(
                CHANNEL_TASK_REMINDERS,
                context.getString(R.string.enable_task_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming study tasks and assignments"
                enableVibration(true)
            }

            val recurringChannel = NotificationChannel(
                CHANNEL_RECURRING_REMINDERS,
                context.getString(R.string.enable_recurring_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recurring study schedule and habit reminders"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(recurringChannel)
        }
    }

    fun showTaskReminderNotification(
        context: Context,
        taskId: Long,
        title: String,
        description: String?
    ) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("studyflow://task_details/$taskId"),
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ("task_$taskId").hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_TASK_REMINDERS)
            .setSmallIcon(R.drawable.ic_studyflow_logo)
            .setContentTitle(title)
            .setContentText(description ?: context.getString(R.string.task_details_title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(("task_$taskId").hashCode(), builder.build())
    }

    fun showRecurringReminderNotification(
        context: Context,
        reminderId: Long,
        title: String,
        message: String?
    ) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("studyflow://reminders"),
            context,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ("recurring_$reminderId").hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_RECURRING_REMINDERS)
            .setSmallIcon(R.drawable.ic_studyflow_logo)
            .setContentTitle(title)
            .setContentText(message ?: context.getString(R.string.reminders_title))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(("recurring_$reminderId").hashCode(), builder.build())
    }
}
