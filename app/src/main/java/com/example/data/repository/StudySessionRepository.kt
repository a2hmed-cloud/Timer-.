package com.example.data.repository

import com.example.data.entity.PomodoroSessionType
import com.example.data.entity.StudySession
import com.example.data.local.StudySessionDao
import kotlinx.coroutines.flow.Flow

class StudySessionRepository(private val studySessionDao: StudySessionDao) {

    fun observeAllSessions(): Flow<List<StudySession>> = studySessionDao.observeAllSessions()

    fun observeSessionsForTask(taskId: Long): Flow<List<StudySession>> =
        studySessionDao.observeSessionsForTask(taskId)

    fun observeSessionsBetween(startTime: Long, endTime: Long): Flow<List<StudySession>> =
        studySessionDao.observeSessionsBetween(startTime, endTime)

    fun observeSessionsBetweenWithType(
        startTime: Long,
        endTime: Long,
        type: PomodoroSessionType = PomodoroSessionType.FOCUS
    ): Flow<List<StudySession>> =
        studySessionDao.observeSessionsBetweenWithType(startTime, endTime, type)

    fun observeTotalFocusMinutesBetween(startTime: Long, endTime: Long): Flow<Int?> =
        studySessionDao.observeTotalFocusMinutesBetween(startTime, endTime)

    suspend fun insertSession(session: StudySession): Long = studySessionDao.insertSession(session)

    suspend fun deleteSession(session: StudySession) = studySessionDao.deleteSession(session)

    suspend fun clearAllSessions() = studySessionDao.clearAllSessions()
}
