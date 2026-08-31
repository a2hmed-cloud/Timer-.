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
import com.example.data.repository.UserPreferencesRepository
import com.example.notification.TaskReminderScheduler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditTaskUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val subjectId: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val estimatedMinutesText: String = "",
    val dueAt: Long? = null,
    val scheduledAt: Long? = null,
    val reminderOffsetMinutes: Int? = 15,
    val availableSubjects: List<Subject> = emptyList(),
    val titleError: String? = null,
    val estimatedError: String? = null,
    val isSaving: Boolean = false
)

sealed interface AddEditTaskEvent {
    data object SaveSuccess : AddEditTaskEvent
    data class ShowError(val message: String) : AddEditTaskEvent
}

class AddEditTaskViewModel(
    private val taskId: Long?,
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val taskReminderScheduler: TaskReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTaskUiState(taskId = taskId))
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditTaskEvent>()
    val events: SharedFlow<AddEditTaskEvent> = _events.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val subjects = subjectRepository.getAllSubjects()
            _uiState.update { it.copy(availableSubjects = subjects) }

            if (taskId != null && taskId > 0) {
                val task = taskRepository.getTaskById(taskId)
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description ?: "",
                            subjectId = task.subjectId,
                            priority = task.priority,
                            status = task.status,
                            estimatedMinutesText = task.estimatedMinutes?.toString() ?: "",
                            dueAt = task.dueAt,
                            scheduledAt = task.scheduledAt,
                            reminderOffsetMinutes = task.reminderOffsetMinutes
                        )
                    }
                }
            } else {
                // Default reminder offset from preferences
                val prefs = userPreferencesRepository.userPreferencesFlow.firstOrNull()
                if (prefs != null) {
                    _uiState.update {
                        it.copy(reminderOffsetMinutes = prefs.defaultReminderOffsetMinutes)
                    }
                }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    fun onSubjectSelected(subjectId: Long?) {
        _uiState.update { it.copy(subjectId = subjectId) }
    }

    fun onPrioritySelected(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onEstimatedMinutesChange(text: String) {
        _uiState.update { it.copy(estimatedMinutesText = text, estimatedError = null) }
    }

    fun onDueDateSelected(timestamp: Long?) {
        _uiState.update { it.copy(dueAt = timestamp) }
    }

    fun onScheduledTimeSelected(timestamp: Long?) {
        _uiState.update { it.copy(scheduledAt = timestamp) }
    }

    fun onReminderOffsetSelected(minutes: Int?) {
        _uiState.update { it.copy(reminderOffsetMinutes = minutes) }
    }

    fun saveTask() {
        val state = _uiState.value
        val titleTrimmed = state.title.trim()

        if (titleTrimmed.isEmpty()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        var estimatedMinutes: Int? = null
        if (state.estimatedMinutesText.isNotBlank()) {
            val parsed = state.estimatedMinutesText.toIntOrNull()
            if (parsed == null || parsed <= 0) {
                _uiState.update { it.copy(estimatedError = "Must be greater than 0") }
                return
            }
            estimatedMinutes = parsed
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val task = Task(
                    id = state.taskId ?: 0L,
                    title = titleTrimmed,
                    description = state.description.trim().ifEmpty { null },
                    subjectId = state.subjectId,
                    priority = state.priority,
                    status = state.status,
                    estimatedMinutes = estimatedMinutes,
                    dueAt = state.dueAt,
                    scheduledAt = state.scheduledAt,
                    reminderOffsetMinutes = state.reminderOffsetMinutes,
                    createdAt = System.currentTimeMillis()
                )

                if (task.id > 0) {
                    taskRepository.updateTask(task)
                    taskReminderScheduler.cancelTaskReminder(task.id)
                    taskReminderScheduler.scheduleTaskReminder(task)
                } else {
                    val newId = taskRepository.insertTask(task)
                    taskReminderScheduler.scheduleTaskReminder(task.copy(id = newId))
                }

                _events.emit(AddEditTaskEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(AddEditTaskEvent.ShowError(e.message ?: "Failed to save task"))
            }
        }
    }

    class Factory(
        private val taskId: Long?,
        private val taskRepository: TaskRepository,
        private val subjectRepository: SubjectRepository,
        private val userPreferencesRepository: UserPreferencesRepository,
        private val taskReminderScheduler: TaskReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditTaskViewModel(
                taskId,
                taskRepository,
                subjectRepository,
                userPreferencesRepository,
                taskReminderScheduler
            ) as T
        }
    }
}
