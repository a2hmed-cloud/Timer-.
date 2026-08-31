package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1,

    val name: String = "",

    val educationCountryId: String? = null,

    val educationCountryName: String? = null,

    val educationSystemId: String? = null,

    val educationSystemName: String? = null,

    val gradeId: String? = null,

    val gradeName: String? = null,

    val onboardingCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis()
)
