package com.example.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Priority
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import com.example.data.repository.SubjectRepository
import com.example.data.repository.TaskRepository
import com.example.domain.planner.TaskPriorityCalculator
import com.example.notification.TaskReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TaskFilter {
    ALL,
    ACTIVE,
    COMPLETED,
    HIGH_PRIORITY,
    TODAY,
    UPCOMING
}

data class TasksUiState(
    val isLoading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val subjects: Map<Long, Subject> = emptyMap(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = ""
)

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TasksUiState> = combine(
        taskRepository.observeAllTasks(),
        subjectRepository.observeAllSubjects(),
        _selectedFilter,
        _searchQuery
    ) { allTasks, subjectsList, filter, query ->
        val subjectsMap = subjectsList.associateBy { it.id }

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endOfToday = cal.timeInMillis

        val filteredByTab = when (filter) {
            TaskFilter.ALL -> allTasks
            TaskFilter.ACTIVE -> allTasks.filter { it.status != TaskStatus.COMPLETED }
            TaskFilter.COMPLETED -> allTasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilter.HIGH_PRIORITY -> allTasks.filter { it.priority == Priority.HIGH }
            TaskFilter.TODAY -> allTasks.filter {
                (it.dueAt != null && it.dueAt in startOfToday..endOfToday) ||
                (it.scheduledAt != null && it.scheduledAt in startOfToday..endOfToday) ||
                (it.createdAt in startOfToday..endOfToday)
            }
            TaskFilter.UPCOMING -> allTasks.filter {
                val due = it.dueAt ?: it.scheduledAt
                due != null && due > endOfToday && it.status != TaskStatus.COMPLETED
            }
        }

        val filteredByQuery = if (query.isBlank()) {
            filteredByTab
        } else {
            filteredByTab.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                (task.description?.contains(query, ignoreCase = true) == true) ||
                (task.subjectId?.let { subjectsMap[it]?.name?.contains(query, ignoreCase = true) } == true)
            }
        }

        val sorted = TaskPriorityCalculator.sortTasks(filteredByQuery)

        TasksUiState(
            isLoading = false,
            tasks = sorted,
            subjects = subjectsMap,
            selectedFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasksUiState()
    )

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

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

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskReminderScheduler.cancelTaskReminder(task.id)
            taskRepository.deleteTask(task)
        }
    }

    class Factory(
        private val taskRepository: TaskRepository,
        private val subjectRepository: SubjectRepository,
        private val taskReminderScheduler: TaskReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TasksViewModel(taskRepository, subjectRepository, taskReminderScheduler) as T
        }
    }
}
