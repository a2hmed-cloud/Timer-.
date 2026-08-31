package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long = 1,

    val name: String,

    val catalogSubjectId: String? = null,

    val color: Long? = null,

    val icon: String? = null,

    val isActive: Boolean = true,

    val sortOrder: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)
