package com.example.domain.planner

import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import java.util.Calendar

object DailyPlanner {

    data class DailyStats(
        val totalTasks: Int,
        val completedTasks: Int,
        val pendingTasks: Int,
        val progressPercentage: Int,
        val estimatedTotalMinutes: Int,
        val estimatedRemainingMinutes: Int
    )

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun filterTodayTasks(tasks: List<Task>, currentTime: Long = System.currentTimeMillis()): List<Task> {
        val start = getStartOfDay(currentTime)
        val end = getEndOfDay(currentTime)

        return tasks.filter { task ->
            val isDueToday = task.dueAt != null && task.dueAt in start..end
            val isScheduledToday = task.scheduledAt != null && task.scheduledAt in start..end
            val isOverdueAndActive = task.dueAt != null && task.dueAt < start && task.status != TaskStatus.COMPLETED
            val isCreatedTodayAndActive = task.createdAt in start..end && task.status != TaskStatus.COMPLETED
            val isActiveGeneral = task.status == TaskStatus.IN_PROGRESS ||
                    (task.status == TaskStatus.TODO && task.dueAt == null && task.scheduledAt == null)

            isDueToday || isScheduledToday || isOverdueAndActive || isCreatedTodayAndActive || isActiveGeneral
        }
    }

    fun computeDailyStats(tasks: List<Task>): DailyStats {
        val total = tasks.size
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val pending = total - completed
        val progress = ProgressCalculator.calculatePercentage(completed, total)
        val estTotal = tasks.mapNotNull { it.estimatedMinutes }.sum()
        val estRemaining = tasks.filter { it.status != TaskStatus.COMPLETED }.mapNotNull { it.estimatedMinutes }.sum()

        return DailyStats(
            totalTasks = total,
            completedTasks = completed,
            pendingTasks = pending,
            progressPercentage = progress,
            estimatedTotalMinutes = estTotal,
            estimatedRemainingMinutes = estRemaining
        )
    }
}
