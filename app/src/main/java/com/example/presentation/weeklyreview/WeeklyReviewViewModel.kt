package com.example.presentation.weeklyreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.TaskStatus
import com.example.data.repository.StudySessionRepository
import com.example.data.repository.TaskRepository
import com.example.domain.planner.ProgressCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class WeeklyReviewUiState(
    val isLoading: Boolean = true,
    val completedTasksCount: Int = 0,
    val createdTasksCount: Int = 0,
    val totalStudyTimeMinutes: Int = 0,
    val completionRatePercentage: Int = 0,
    val hasEnoughData: Boolean = false
)

class WeeklyReviewViewModel(
    private val taskRepository: TaskRepository,
    private val studySessionRepository: StudySessionRepository
) : ViewModel() {

    val uiState: StateFlow<WeeklyReviewUiState> = combine(
        taskRepository.observeAllTasks(),
        studySessionRepository.observeAllSessions()
    ) { allTasks, allSessions ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val sevenDaysAgo = cal.timeInMillis

        val tasksInPastWeek = allTasks.filter {
            it.createdAt >= sevenDaysAgo || (it.completedAt != null && it.completedAt >= sevenDaysAgo)
        }

        val sessionsInPastWeek = allSessions.filter {
            it.startedAt >= sevenDaysAgo
        }

        val createdCount = tasksInPastWeek.count { it.createdAt >= sevenDaysAgo }
        val completedCount = tasksInPastWeek.count { it.status == TaskStatus.COMPLETED && (it.completedAt ?: 0L) >= sevenDaysAgo }
        val totalStudyMinutes = sessionsInPastWeek.sumOf { it.durationMinutes }

        val totalConsidered = createdCount.coerceAtLeast(completedCount)
        val completionRate = if (totalConsidered > 0) {
            ProgressCalculator.calculatePercentage(completedCount, totalConsidered)
        } else 0

        val hasData = (createdCount > 0 || completedCount > 0 || totalStudyMinutes > 0)

        WeeklyReviewUiState(
            isLoading = false,
            completedTasksCount = completedCount,
            createdTasksCount = createdCount,
            totalStudyTimeMinutes = totalStudyMinutes,
            completionRatePercentage = completionRate,
            hasEnoughData = hasData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WeeklyReviewUiState()
    )

    class Factory(
        private val taskRepository: TaskRepository,
        private val studySessionRepository: StudySessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeeklyReviewViewModel(taskRepository, studySessionRepository) as T
        }
    }
}
