package com.example

import com.example.data.entity.Priority
import com.example.data.entity.RecurringReminder
import com.example.data.entity.RepeatType
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import com.example.domain.planner.ProgressCalculator
import com.example.domain.planner.RecurringScheduleCalculator
import com.example.domain.planner.TaskPriorityCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun progressCalculator_calculatesCorrectPercentage() {
        assertEquals(0, ProgressCalculator.calculatePercentage(0, 0))
        assertEquals(50, ProgressCalculator.calculatePercentage(1, 2))
        assertEquals(100, ProgressCalculator.calculatePercentage(5, 5))
        assertEquals(33, ProgressCalculator.calculatePercentage(1, 3))
    }

    @Test
    fun taskPriorityCalculator_prioritizesUrgentTasks() {
        val now = System.currentTimeMillis()
        val lowPriorityTask = Task(
            id = 1,
            title = "Low Priority",
            priority = Priority.LOW,
            createdAt = now
        )
        val highPriorityTask = Task(
            id = 2,
            title = "High Priority",
            priority = Priority.HIGH,
            dueAt = now + 3600000L, // Due in 1 hour
            createdAt = now
        )
        val completedTask = Task(
            id = 3,
            title = "Completed",
            priority = Priority.HIGH,
            status = TaskStatus.COMPLETED,
            createdAt = now
        )

        val list = listOf(lowPriorityTask, completedTask, highPriorityTask)
        val sorted = TaskPriorityCalculator.sortTasks(list, now)

        // The highest priority and soonest due active task should be first
        assertEquals(highPriorityTask.id, sorted[0].id)
        assertEquals(lowPriorityTask.id, sorted[1].id)
        // Completed task should be at the bottom
        assertEquals(completedTask.id, sorted[2].id)

        val nextTask = TaskPriorityCalculator.getNextTask(list, now)
        assertNotNull(nextTask)
        assertEquals(highPriorityTask.id, nextTask?.id)
    }

    @Test
    fun recurringScheduleCalculator_formatsAndParsesDays() {
        val days = setOf(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY)
        val iso = RecurringScheduleCalculator.formatIsoDays(days)
        val parsed = RecurringScheduleCalculator.parseDaysOfWeek(iso)

        assertEquals(days, parsed)
    }

    @Test
    fun parseSubjectColor_convertsHexAndFallbackCorrectly() {
        val fallbackColor = androidx.compose.ui.graphics.Color.Red

        // Null returns fallback
        assertEquals(fallbackColor, com.example.ui.theme.parseSubjectColor(null, fallbackColor))

        // 0L returns fallback
        assertEquals(fallbackColor, com.example.ui.theme.parseSubjectColor(0L, fallbackColor))

        // Valid ARGB Long produces valid non-transparent Color
        val colorBlue = com.example.ui.theme.parseSubjectColor(0xFF2563EBL, fallbackColor)
        assertEquals(1.0f, colorBlue.alpha, 0.01f)
        assertEquals(0.145f, colorBlue.red, 0.05f)
        assertEquals(0.388f, colorBlue.green, 0.05f)
        assertEquals(0.921f, colorBlue.blue, 0.05f)

        // Long without alpha (e.g. 0x2563EB) gets alpha auto-populated
        val colorNoAlpha = com.example.ui.theme.parseSubjectColor(0x2563EBL, fallbackColor)
        assertEquals(1.0f, colorNoAlpha.alpha, 0.01f)
        assertEquals(colorBlue, colorNoAlpha)
    }
}
