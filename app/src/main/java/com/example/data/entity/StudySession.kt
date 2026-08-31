package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("taskId"), Index("startedAt"), Index("userId")]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long = 1,

    val taskId: Long? = null,

    val subjectId: Long? = null,

    val startedAt: Long = System.currentTimeMillis(),

    val endedAt: Long? = null,

    val durationMinutes: Int = 0,

    val sessionType: PomodoroSessionType = PomodoroSessionType.FOCUS,

    val completed: Boolean = true
)
