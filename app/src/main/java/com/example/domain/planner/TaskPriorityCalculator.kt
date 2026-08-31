package com.example.domain.planner

import com.example.data.entity.Priority
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus

/**
 * Independent priority calculator for sorting tasks intelligently.
 * Can be extended or replaced by AI models in future releases.
 */
object TaskPriorityCalculator {

    /**
     * Calculates an urgency score for a task (higher score = more urgent).
     * Completed tasks get a negative score to always appear at the end.
     */
    fun calculateScore(task: Task, currentTime: Long = System.currentTimeMillis()): Double {
        if (task.status == TaskStatus.COMPLETED) {
            return -1000.0 - (task.completedAt ?: task.createdAt)
        }

        var score = 0.0

        // 1. Priority baseline
        score += when (task.priority) {
            Priority.HIGH -> 300.0
            Priority.MEDIUM -> 200.0
            Priority.LOW -> 100.0
        }

        // 2. Status bonus (in progress is already active)
        if (task.status == TaskStatus.IN_PROGRESS) {
            score += 50.0
        }

        // 3. Due date urgency
        if (task.dueAt != null) {
            val millisUntilDue = task.dueAt - currentTime
            val hoursUntilDue = millisUntilDue / (1000.0 * 60 * 60)

            score += when {
                hoursUntilDue < 0 -> 500.0 + (-hoursUntilDue).coerceAtMost(200.0) // Overdue tasks get top priority
                hoursUntilDue <= 6 -> 250.0
                hoursUntilDue <= 24 -> 150.0
                hoursUntilDue <= 72 -> 80.0
                else -> (500.0 / (hoursUntilDue + 1)).coerceAtLeast(0.0)
            }
        }

        // 4. Scheduled time urgency
        if (task.scheduledAt != null) {
            val millisUntilScheduled = task.scheduledAt - currentTime
            val hoursUntilScheduled = millisUntilScheduled / (1000.0 * 60 * 60)
            if (hoursUntilScheduled in -2.0..2.0) {
                score += 120.0 // Currently scheduled window
            } else if (hoursUntilScheduled in 0.0..12.0) {
                score += 60.0
            }
        }

        // 5. Short duration quick-wins (tasks under 30 mins get small boost)
        task.estimatedMinutes?.let { minutes ->
            if (minutes in 1..30) {
                score += (30 - minutes) * 0.5
            }
        }

        return score
    }

    /**
     * Sorts tasks based on the smart ordering rules:
     * 1. Completed status (completed tasks at the bottom)
     * 2. Overdue & Due Date (due sooner before due later)
     * 3. Priority (High > Medium > Low)
     * 4. Scheduled Time (sooner before later)
     * 5. Estimated Duration (shorter before longer)
     */
    fun sortTasks(tasks: List<Task>, currentTime: Long = System.currentTimeMillis()): List<Task> {
        return tasks.sortedWith(
            compareBy<Task> { it.status == TaskStatus.COMPLETED }
                .thenByDescending { calculateScore(it, currentTime) }
                .thenBy { it.dueAt ?: Long.MAX_VALUE }
                .thenByDescending { it.priority.ordinal }
                .thenBy { it.scheduledAt ?: Long.MAX_VALUE }
                .thenBy { it.estimatedMinutes ?: Int.MAX_VALUE }
                .thenByDescending { it.createdAt }
        )
    }

    /**
     * Determines the single "Next Task" to spotlight for the student.
     * Returns null if no active tasks exist.
     */
    fun getNextTask(tasks: List<Task>, currentTime: Long = System.currentTimeMillis()): Task? {
        val activeTasks = tasks.filter { it.status != TaskStatus.COMPLETED }
        if (activeTasks.isEmpty()) return null
        return sortTasks(activeTasks, currentTime).firstOrNull()
    }
}
