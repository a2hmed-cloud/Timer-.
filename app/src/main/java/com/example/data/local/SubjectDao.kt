package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY sortOrder ASC, name ASC")
    fun observeAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun observeActiveSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects ORDER BY sortOrder ASC, name ASC")
    suspend fun getAllSubjects(): List<Subject>

    @Query("SELECT * FROM subjects WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    suspend fun getActiveSubjects(): List<Subject>

    @Query("SELECT * FROM subjects WHERE id = :id")
    fun observeSubjectById(id: Long): Flow<Subject?>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>): List<Long>

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)

    @Query("DELETE FROM subjects")
    suspend fun clearAllSubjects()
}
