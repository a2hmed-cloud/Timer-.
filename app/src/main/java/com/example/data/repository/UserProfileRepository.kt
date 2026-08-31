package com.example.data.repository

import com.example.data.entity.UserProfile
import com.example.data.local.UserProfileDao
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun getProfileOnce(): UserProfile? = userProfileDao.getUserProfileOnce()

    suspend fun saveProfile(profile: UserProfile): Long {
        return userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateProfile(profile)
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        userProfileDao.setOnboardingCompleted(completed)
    }

    suspend fun clearProfile() {
        userProfileDao.clearProfile()
    }
}
