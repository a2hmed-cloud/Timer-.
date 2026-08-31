package com.example.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.PomodoroSessionType
import com.example.data.entity.StudySession
import com.example.data.entity.Task
import com.example.data.repository.StudySessionRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerState {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

data class FocusUiState(
    val task: Task? = null,
    val availableTasks: List<Task> = emptyList(),
    val sessionType: PomodoroSessionType = PomodoroSessionType.FOCUS,
    val timerState: TimerState = TimerState.IDLE,
    val elapsedSeconds: Long = 0L,
    val targetSeconds: Long = 25 * 60L,
    val completedSessionsCount: Int = 0,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val sessionsBeforeLongBreak: Int = 4,
    val sessionSaved: Boolean = false
)

sealed interface FocusEvent {
    data class SessionFinished(val durationMinutes: Int, val sessionType: PomodoroSessionType) : FocusEvent
}

class FocusViewModel(
    private val initialTaskId: Long,
    private val taskRepository: TaskRepository,
    private val studySessionRepository: StudySessionRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FocusEvent>()
    val events: SharedFlow<FocusEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

    init {
        loadPreferencesAndTask()
        loadAvailableTasks()
    }

    private fun loadPreferencesAndTask() {
        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            val task = if (initialTaskId > 0) taskRepository.getTaskById(initialTaskId) else null

            val targetMin = when (_uiState.value.sessionType) {
                PomodoroSessionType.FOCUS -> task?.estimatedMinutes ?: prefs.focusDurationMinutes
                PomodoroSessionType.SHORT_BREAK -> prefs.shortBreakMinutes
                PomodoroSessionType.LONG_BREAK -> prefs.longBreakMinutes
            }

            _uiState.update {
                it.copy(
                    task = task,
                    focusDurationMinutes = prefs.focusDurationMinutes,
                    shortBreakMinutes = prefs.shortBreakMinutes,
                    longBreakMinutes = prefs.longBreakMinutes,
                    sessionsBeforeLongBreak = prefs.sessionsBeforeLongBreak,
                    targetSeconds = (targetMin.coerceAtLeast(1) * 60).toLong()
                )
            }
        }
    }

    private fun loadAvailableTasks() {
        viewModelScope.launch {
            taskRepository.observeAllTasks().collect { tasks ->
                _uiState.update {
                    it.copy(availableTasks = tasks.filter { t -> t.status != com.example.data.entity.TaskStatus.COMPLETED })
                }
            }
        }
    }

    fun selectTask(task: Task?) {
        _uiState.update { it.copy(task = task) }
    }

    fun switchSessionType(type: PomodoroSessionType) {
        timerJob?.cancel()
        val targetMin = when (type) {
            PomodoroSessionType.FOCUS -> _uiState.value.task?.estimatedMinutes ?: _uiState.value.focusDurationMinutes
            PomodoroSessionType.SHORT_BREAK -> _uiState.value.shortBreakMinutes
            PomodoroSessionType.LONG_BREAK -> _uiState.value.longBreakMinutes
        }
        _uiState.update {
            it.copy(
                sessionType = type,
                timerState = TimerState.IDLE,
                elapsedSeconds = 0L,
                targetSeconds = (targetMin.coerceAtLeast(1) * 60).toLong(),
                sessionSaved = false
            )
        }
    }

    fun startTimer() {
        if (_uiState.value.timerState == TimerState.IDLE) {
            sessionStartTime = System.currentTimeMillis()
        }
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTicker()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    fun resumeTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startTicker()
    }

    fun finishSession(markTaskComplete: Boolean = false) {
        timerJob?.cancel()
        val elapsed = _uiState.value.elapsedSeconds
        val durationMinutes = ((elapsed + 59) / 60).toInt().coerceAtLeast(1)
        val endedAt = System.currentTimeMillis()
        val currentType = _uiState.value.sessionType
        val selectedTask = _uiState.value.task

        viewModelScope.launch {
            if (sessionStartTime == 0L) {
                sessionStartTime = endedAt - (elapsed * 1000L)
            }
            val session = StudySession(
                taskId = selectedTask?.id,
                subjectId = selectedTask?.subjectId,
                startedAt = sessionStartTime,
                endedAt = endedAt,
                durationMinutes = durationMinutes,
                sessionType = currentType,
                completed = true
            )
            studySessionRepository.insertSession(session)

            if (markTaskComplete && selectedTask != null) {
                taskRepository.completeTask(selectedTask.id)
            }

            val newCompletedCount = if (currentType == PomodoroSessionType.FOCUS) {
                _uiState.value.completedSessionsCount + 1
            } else {
                _uiState.value.completedSessionsCount
            }

            _uiState.update {
                it.copy(
                    timerState = TimerState.COMPLETED,
                    sessionSaved = true,
                    completedSessionsCount = newCompletedCount
                )
            }
            _events.emit(FocusEvent.SessionFinished(durationMinutes, currentType))
        }
    }

    fun nextSession() {
        val current = _uiState.value
        val nextType = when (current.sessionType) {
            PomodoroSessionType.FOCUS -> {
                if (current.completedSessionsCount % current.sessionsBeforeLongBreak == 0 && current.completedSessionsCount > 0) {
                    PomodoroSessionType.LONG_BREAK
                } else {
                    PomodoroSessionType.SHORT_BREAK
                }
            }
            PomodoroSessionType.SHORT_BREAK, PomodoroSessionType.LONG_BREAK -> PomodoroSessionType.FOCUS
        }
        switchSessionType(nextType)
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerState == TimerState.RUNNING) {
                delay(1000)
                _uiState.update {
                    it.copy(elapsedSeconds = it.elapsedSeconds + 1)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    class Factory(
        private val taskId: Long,
        private val taskRepository: TaskRepository,
        private val studySessionRepository: StudySessionRepository,
        private val preferencesRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FocusViewModel(
                taskId,
                taskRepository,
                studySessionRepository,
                preferencesRepository
            ) as T
        }
    }
}
