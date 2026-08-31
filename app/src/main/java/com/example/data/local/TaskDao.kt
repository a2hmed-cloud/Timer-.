package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun observeTaskById(id: Long): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE scheduledAt IS NOT NULL ORDER BY scheduledAt ASC")
    fun observeScheduledTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' AND reminderOffsetMinutes IS NOT NULL AND (dueAt IS NOT NULL OR scheduledAt IS NOT NULL)")
    suspend fun getActiveTasksWithReminders(): List<Task>

    @Query("SELECT * FROM tasks WHERE (dueAt BETWEEN :startOfDay AND :endOfDay) OR (scheduledAt BETWEEN :startOfDay AND :endOfDay) OR (createdAt BETWEEN :startOfDay AND :endOfDay) OR status != 'COMPLETED'")
    fun observeTodayTasks(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE createdAt BETWEEN :startTime AND :endTime OR completedAt BETWEEN :startTime AND :endTime")
    fun observeTasksBetween(startTime: Long, endTime: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}
