package com.example.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("subjectId"), Index("status"), Index("dueAt"), Index("scheduledAt"), Index("userId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long = 1,

    val title: String,

    val description: String? = null,

    val subjectId: Long? = null,

    val priority: Priority = Priority.MEDIUM,

    val status: TaskStatus = TaskStatus.TODO,

    val estimatedMinutes: Int? = null,

    val dueAt: Long? = null,

    val scheduledAt: Long? = null,

    val reminderOffsetMinutes: Int? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val completedAt: Long? = null
)
