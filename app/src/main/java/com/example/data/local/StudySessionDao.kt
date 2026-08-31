package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.PomodoroSessionType
import com.example.data.entity.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startedAt DESC")
    fun observeAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE taskId = :taskId ORDER BY startedAt DESC")
    fun observeSessionsForTask(taskId: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE startedAt BETWEEN :startTime AND :endTime ORDER BY startedAt DESC")
    fun observeSessionsBetween(startTime: Long, endTime: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE startedAt BETWEEN :startTime AND :endTime AND sessionType = :sessionType ORDER BY startedAt DESC")
    fun observeSessionsBetweenWithType(
        startTime: Long,
        endTime: Long,
        sessionType: PomodoroSessionType = PomodoroSessionType.FOCUS
    ): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE startedAt BETWEEN :startTime AND :endTime AND sessionType = 'FOCUS' AND completed = 1")
    fun observeTotalFocusMinutesBetween(startTime: Long, endTime: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("DELETE FROM study_sessions")
    suspend fun clearAllSessions()
}
