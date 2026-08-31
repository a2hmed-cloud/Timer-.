package com.example.presentation.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import com.example.data.entity.UserProfile
import com.example.data.repository.StudySessionRepository
import com.example.data.repository.SubjectRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserProfileRepository
import com.example.domain.planner.DailyPlanner
import com.example.domain.planner.PomodoroCalculator
import com.example.domain.planner.ProgressCalculator
import com.example.domain.planner.TaskPriorityCalculator
import com.example.notification.TaskReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val tasks: List<Task> = emptyList(),
    val subjects: Map<Long, Subject> = emptyMap(),
    val nextTask: Task? = null,
    val totalTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val progress: Float = 0f,
    val progressPercentage: Int = 0,
    val todayStudyMinutes: Int = 0
)

class TodayViewModel(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val userProfileRepository: UserProfileRepository,
    private val studySessionRepository: StudySessionRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val startOfDay = DailyPlanner.getStartOfDay()
    private val endOfDay = DailyPlanner.getEndOfDay()

    val uiState: StateFlow<TodayUiState> = combine(
        taskRepository.observeAllTasks(),
        subjectRepository.observeAllSubjects(),
        userProfileRepository.userProfile,
        studySessionRepository.observeTotalFocusMinutesBetween(startOfDay, endOfDay)
    ) { allTasks, subjectsList, profile, totalFocusMin ->
        val subjectsMap = subjectsList.associateBy { it.id }
        val todayTasks = DailyPlanner.filterTodayTasks(allTasks)

        val sortedTasks = TaskPriorityCalculator.sortTasks(todayTasks)
        val nextTask = TaskPriorityCalculator.getNextTask(todayTasks)

        val totalCount = todayTasks.size
        val completedCount = todayTasks.count { it.status == TaskStatus.COMPLETED }
        val progress = ProgressCalculator.calculateProgress(completedCount, totalCount)
        val progressPercentage = ProgressCalculator.calculatePercentage(completedCount, totalCount)

        TodayUiState(
            isLoading = false,
            userProfile = profile,
            tasks = sortedTasks,
            subjects = subjectsMap,
            nextTask = nextTask,
            totalTasksCount = totalCount,
            completedTasksCount = completedCount,
            progress = progress,
            progressPercentage = progressPercentage,
            todayStudyMinutes = totalFocusMin ?: 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodayUiState()
    )

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            if (task.status == TaskStatus.COMPLETED) {
                taskRepository.uncompleteTask(task.id)
                taskReminderScheduler.scheduleTaskReminder(task.copy(status = TaskStatus.TODO))
            } else {
                taskRepository.completeTask(task.id)
                taskReminderScheduler.cancelTaskReminder(task.id)
            }
        }
    }

    class Factory(
        private val taskRepository: TaskRepository,
        private val subjectRepository: SubjectRepository,
        private val userProfileRepository: UserProfileRepository,
        private val studySessionRepository: StudySessionRepository,
        private val taskReminderScheduler: TaskReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodayViewModel(
                taskRepository,
                subjectRepository,
                userProfileRepository,
                studySessionRepository,
                taskReminderScheduler
            ) as T
        }
    }
}
