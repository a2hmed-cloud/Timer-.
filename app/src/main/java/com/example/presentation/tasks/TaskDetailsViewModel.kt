package com.example.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import com.example.data.repository.SubjectRepository
import com.example.data.repository.TaskRepository
import com.example.notification.TaskReminderScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskDetailsUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
    val subject: Subject? = null
)

sealed interface TaskDetailsEvent {
    data object TaskDeleted : TaskDetailsEvent
}

class TaskDetailsViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState: StateFlow<TaskDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailsEvent>()
    val events: SharedFlow<TaskDetailsEvent> = _events.asSharedFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            taskRepository.observeTaskById(taskId).collect { task ->
                if (task != null) {
                    val subject = task.subjectId?.let { subjectRepository.getSubjectById(it) }
                    _uiState.value = TaskDetailsUiState(
                        isLoading = false,
                        task = task,
                        subject = subject
                    )
                } else {
                    _uiState.value = TaskDetailsUiState(isLoading = false, task = null)
                }
            }
        }
    }

    fun toggleComplete() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            if (currentTask.status == TaskStatus.COMPLETED) {
                taskRepository.uncompleteTask(currentTask.id)
                taskReminderScheduler.scheduleTaskReminder(currentTask.copy(status = TaskStatus.TODO))
            } else {
                taskRepository.completeTask(currentTask.id)
                taskReminderScheduler.cancelTaskReminder(currentTask.id)
            }
        }
    }

    fun deleteTask() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            taskReminderScheduler.cancelTaskReminder(currentTask.id)
            taskRepository.deleteTask(currentTask)
            _events.emit(TaskDetailsEvent.TaskDeleted)
        }
    }

    class Factory(
        private val taskId: Long,
        private val taskRepository: TaskRepository,
        private val subjectRepository: SubjectRepository,
        private val taskReminderScheduler: TaskReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskDetailsViewModel(taskId, taskRepository, subjectRepository, taskReminderScheduler) as T
        }
    }
}
