package com.example.data.entity

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED
}

enum class RepeatType {
    ONCE,
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}

enum class PomodoroSessionType {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}

enum class ColorAccent {
    DYNAMIC,
    BLUE,
    GREEN,
    PURPLE,
    AMBER,
    ROSE
}
