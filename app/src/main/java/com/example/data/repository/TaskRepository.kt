package com.example.data.repository

import com.example.data.entity.Task
import com.example.data.entity.TaskStatus
import com.example.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun observeAllTasks(): Flow<List<Task>> = taskDao.observeAllTasks()

    fun observeTaskById(id: Long): Flow<Task?> = taskDao.observeTaskById(id)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    fun observeScheduledTasks(): Flow<List<Task>> = taskDao.observeScheduledTasks()

    fun observeTodayTasks(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.observeTodayTasks(startOfDay, endOfDay)

    fun observeTasksBetween(startTime: Long, endTime: Long): Flow<List<Task>> =
        taskDao.observeTasksBetween(startTime, endTime)

    suspend fun getActiveTasksWithReminders(): List<Task> =
        taskDao.getActiveTasksWithReminders()

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun completeTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        val updated = task.copy(
            status = TaskStatus.COMPLETED,
            completedAt = System.currentTimeMillis()
        )
        taskDao.updateTask(updated)
    }

    suspend fun uncompleteTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId) ?: return
        val updated = task.copy(
            status = TaskStatus.TODO,
            completedAt = null
        )
        taskDao.updateTask(updated)
    }

    suspend fun clearAllTasks() = taskDao.clearAllTasks()
}
