package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.entity.ColorAccent
import com.example.data.entity.PomodoroSessionType
import com.example.data.entity.Priority
import com.example.data.entity.RepeatType
import com.example.data.entity.TaskStatus

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority?): String? = priority?.name

    @TypeConverter
    fun toPriority(value: String?): Priority = value?.let {
        try {
            Priority.valueOf(it)
        } catch (e: Exception) {
            Priority.MEDIUM
        }
    } ?: Priority.MEDIUM

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus?): String? = status?.name

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus = value?.let {
        try {
            TaskStatus.valueOf(it)
        } catch (e: Exception) {
            TaskStatus.TODO
        }
    } ?: TaskStatus.TODO

    @TypeConverter
    fun fromRepeatType(repeatType: RepeatType?): String? = repeatType?.name

    @TypeConverter
    fun toRepeatType(value: String?): RepeatType = value?.let {
        try {
            RepeatType.valueOf(it)
        } catch (e: Exception) {
            RepeatType.DAILY
        }
    } ?: RepeatType.DAILY

    @TypeConverter
    fun fromPomodoroSessionType(sessionType: PomodoroSessionType?): String? = sessionType?.name

    @TypeConverter
    fun toPomodoroSessionType(value: String?): PomodoroSessionType = value?.let {
        try {
            PomodoroSessionType.valueOf(it)
        } catch (e: Exception) {
            PomodoroSessionType.FOCUS
        }
    } ?: PomodoroSessionType.FOCUS

    @TypeConverter
    fun fromColorAccent(accent: ColorAccent?): String? = accent?.name

    @TypeConverter
    fun toColorAccent(value: String?): ColorAccent = value?.let {
        try {
            ColorAccent.valueOf(it)
        } catch (e: Exception) {
            ColorAccent.DYNAMIC
        }
    } ?: ColorAccent.DYNAMIC
}
