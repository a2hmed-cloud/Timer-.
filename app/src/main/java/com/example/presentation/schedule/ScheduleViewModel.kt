package com.example.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.Subject
import com.example.data.entity.Task
import com.example.data.repository.SubjectRepository
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ScheduleHourSlot(
    val hourOfDay: Int,
    val timeLabel: String,
    val tasks: List<Task>
)

data class ScheduleUiState(
    val isLoading: Boolean = true,
    val scheduledTasks: List<Task> = emptyList(),
    val subjects: Map<Long, Subject> = emptyMap(),
    val slots: List<ScheduleHourSlot> = emptyList()
)

class ScheduleViewModel(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    val uiState: StateFlow<ScheduleUiState> = combine(
        taskRepository.observeScheduledTasks(),
        subjectRepository.observeAllSubjects()
    ) { scheduledList, subjectsList ->
        val subjectsMap = subjectsList.associateBy { it.id }

        // Group tasks by scheduled hour
        val timeFormat = SimpleDateFormat("hh:00 a", Locale.getDefault())
        val groupedByHour = scheduledList.groupBy { task ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = task.scheduledAt ?: 0L
            }
            cal.get(Calendar.HOUR_OF_DAY)
        }

        val slots = groupedByHour.map { (hour, tasksInHour) ->
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
            }
            ScheduleHourSlot(
                hourOfDay = hour,
                timeLabel = timeFormat.format(Date(cal.timeInMillis)),
                tasks = tasksInHour.sortedBy { it.scheduledAt }
            )
        }.sortedBy { it.hourOfDay }

        ScheduleUiState(
            isLoading = false,
            scheduledTasks = scheduledList,
            subjects = subjectsMap,
            slots = slots
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScheduleUiState()
    )

    class Factory(
        private val taskRepository: TaskRepository,
        private val subjectRepository: SubjectRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ScheduleViewModel(taskRepository, subjectRepository) as T
        }
    }
}
