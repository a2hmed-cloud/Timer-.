package com.example.data.repository

import com.example.data.entity.Subject
import com.example.data.local.SubjectDao
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val subjectDao: SubjectDao) {

    fun observeAllSubjects(): Flow<List<Subject>> = subjectDao.observeAllSubjects()

    fun observeActiveSubjects(): Flow<List<Subject>> = subjectDao.observeActiveSubjects()

    suspend fun getAllSubjects(): List<Subject> = subjectDao.getAllSubjects()

    suspend fun getActiveSubjects(): List<Subject> = subjectDao.getActiveSubjects()

    fun observeSubjectById(id: Long): Flow<Subject?> = subjectDao.observeSubjectById(id)

    suspend fun getSubjectById(id: Long): Subject? = subjectDao.getSubjectById(id)

    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)

    suspend fun insertSubjects(subjects: List<Subject>): List<Long> = subjectDao.insertSubjects(subjects)

    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)

    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    suspend fun deleteSubjectById(id: Long) = subjectDao.deleteSubjectById(id)

    suspend fun clearAllSubjects() = subjectDao.clearAllSubjects()
}
