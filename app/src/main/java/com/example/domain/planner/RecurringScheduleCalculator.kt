package com.example.domain.planner

import com.example.data.entity.RecurringReminder
import com.example.data.entity.RepeatType
import java.util.Calendar

object RecurringScheduleCalculator {

    /**
     * Computes the next trigger epoch millis for a given recurring reminder.
     * Guaranteed to return a timestamp strictly after [fromTime].
     */
    fun calculateNextTriggerTime(reminder: RecurringReminder, fromTime: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = fromTime
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetHour = reminder.timeOfDayMinutes / 60
        val targetMinute = reminder.timeOfDayMinutes % 60

        when (reminder.repeatType) {
            RepeatType.ONCE -> {
                calendar.set(Calendar.HOUR_OF_DAY, targetHour)
                calendar.set(Calendar.MINUTE, targetMinute)
                if (calendar.timeInMillis <= fromTime) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }

            RepeatType.DAILY -> {
                calendar.set(Calendar.HOUR_OF_DAY, targetHour)
                calendar.set(Calendar.MINUTE, targetMinute)
                if (calendar.timeInMillis <= fromTime) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }

            RepeatType.WEEKLY -> {
                // Repeat every week on the same day as created or specified
                val targetDayOfWeek = parseDaysOfWeek(reminder.daysOfWeek).firstOrNull()
                    ?: calendar.get(Calendar.DAY_OF_WEEK)

                calendar.set(Calendar.HOUR_OF_DAY, targetHour)
                calendar.set(Calendar.MINUTE, targetMinute)
                calendar.set(Calendar.DAY_OF_WEEK, targetDayOfWeek)

                if (calendar.timeInMillis <= fromTime) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }

            RepeatType.MONTHLY -> {
                val targetDayOfMonth = (reminder.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)).coerceIn(1, 28)
                calendar.set(Calendar.DAY_OF_MONTH, targetDayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, targetHour)
                calendar.set(Calendar.MINUTE, targetMinute)

                if (calendar.timeInMillis <= fromTime) {
                    calendar.add(Calendar.MONTH, 1)
                }
                return calendar.timeInMillis
            }

            RepeatType.CUSTOM -> {
                val targetDays = parseDaysOfWeek(reminder.daysOfWeek)
                if (targetDays.isEmpty()) {
                    // Fallback to daily
                    calendar.set(Calendar.HOUR_OF_DAY, targetHour)
                    calendar.set(Calendar.MINUTE, targetMinute)
                    if (calendar.timeInMillis <= fromTime) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    return calendar.timeInMillis
                }

                // Check upcoming 14 days to find the earliest matching day
                var candidate: Long? = null
                for (offset in 0..14) {
                    val testCal = Calendar.getInstance().apply {
                        timeInMillis = fromTime
                        add(Calendar.DAY_OF_YEAR, offset)
                        set(Calendar.HOUR_OF_DAY, targetHour)
                        set(Calendar.MINUTE, targetMinute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val currentDayOfWeek = testCal.get(Calendar.DAY_OF_WEEK)
                    if (targetDays.contains(currentDayOfWeek) && testCal.timeInMillis > fromTime) {
                        candidate = testCal.timeInMillis
                        break
                    }
                }
                return candidate ?: (fromTime + 24 * 3600 * 1000)
            }
        }
    }

    /**
     * Parses days string (e.g. "1,2,3" where 1=Mon..7=Sun or Java Calendar values)
     * Maps to Java Calendar.DAY_OF_WEEK (Calendar.SUNDAY = 1, MONDAY = 2, ...)
     */
    fun parseDaysOfWeek(daysString: String?): Set<Int> {
        if (daysString.isNullOrBlank()) return emptySet()
        val result = mutableSetOf<Int>()
        daysString.split(",").forEach { part ->
            part.trim().toIntOrNull()?.let { isoDay ->
                // ISO: 1 = Mon, 2 = Tue, 3 = Wed, 4 = Thu, 5 = Fri, 6 = Sat, 7 = Sun
                val calendarDay = when (isoDay) {
                    1 -> Calendar.MONDAY
                    2 -> Calendar.TUESDAY
                    3 -> Calendar.WEDNESDAY
                    4 -> Calendar.THURSDAY
                    5 -> Calendar.FRIDAY
                    6 -> Calendar.SATURDAY
                    7 -> Calendar.SUNDAY
                    else -> null
                }
                if (calendarDay != null) {
                    result.add(calendarDay)
                }
            }
        }
        return result
    }

    /**
     * Converts Java Calendar.DAY_OF_WEEK set to ISO string format (e.g. "1,3,5")
     */
    fun formatIsoDays(calendarDays: Set<Int>): String {
        return calendarDays.mapNotNull { calDay ->
            when (calDay) {
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                Calendar.SUNDAY -> 7
                else -> null
            }
        }.sorted().joinToString(",")
    }
}
