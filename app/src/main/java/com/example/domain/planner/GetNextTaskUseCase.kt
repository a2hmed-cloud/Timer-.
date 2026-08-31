package com.example.domain.planner

import com.example.data.entity.Task

class GetNextTaskUseCase {
    operator fun invoke(tasks: List<Task>, currentTime: Long = System.currentTimeMillis()): Task? {
        return TaskPriorityCalculator.getNextTask(tasks, currentTime)
    }
}
