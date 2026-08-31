package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.entity.TaskStatus
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("EXTRA_TASK_ID", -1L)
        val title = intent.getStringExtra("EXTRA_TASK_TITLE") ?: return
        val desc = intent.getStringExtra("EXTRA_TASK_DESC")

        if (taskId <= 0) return

        // Verify task state in database: do not show if completed or deleted
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val task = db.taskDao().getTaskById(taskId)
                if (task != null && task.status != TaskStatus.COMPLETED) {
                    NotificationHelper.showTaskReminderNotification(
                        context = context,
                        taskId = taskId,
                        title = title,
                        description = desc
                    )
                }
            } catch (e: Exception) {
                // Log or gracefully handle error
            } finally {
                pendingResult.finish()
            }
        }
    }
}
