package com.example.domain.planner

import com.example.data.entity.PomodoroSessionType
import com.example.data.entity.StudySession

object PomodoroCalculator {

    data class StudyStats(
        val totalFocusMinutes: Int,
        val totalSessionsCount: Int,
        val shortBreakMinutes: Int,
        val longBreakMinutes: Int
    )

    fun calculateStats(sessions: List<StudySession>): StudyStats {
        val completedSessions = sessions.filter { it.completed }
        val focusMinutes = completedSessions
            .filter { it.sessionType == PomodoroSessionType.FOCUS }
            .sumOf { it.durationMinutes }
        val focusSessionsCount = completedSessions
            .count { it.sessionType == PomodoroSessionType.FOCUS }
        val shortBreakMin = completedSessions
            .filter { it.sessionType == PomodoroSessionType.SHORT_BREAK }
            .sumOf { it.durationMinutes }
        val longBreakMin = completedSessions
            .filter { it.sessionType == PomodoroSessionType.LONG_BREAK }
            .sumOf { it.durationMinutes }

        return StudyStats(
            totalFocusMinutes = focusMinutes,
            totalSessionsCount = focusSessionsCount,
            shortBreakMinutes = shortBreakMin,
            longBreakMinutes = longBreakMin
        )
    }

    fun formatDurationMinutes(totalMinutes: Int, isArabic: Boolean = false): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (isArabic) {
            when {
                hours > 0 && minutes > 0 -> "$hours س و $minutes د"
                hours > 0 -> "$hours س"
                else -> "$minutes د"
            }
        } else {
            when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }
        }
    }

    fun formatTimerTime(remainingSeconds: Int): String {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
