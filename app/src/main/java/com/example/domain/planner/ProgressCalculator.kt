package com.example.domain.planner

object ProgressCalculator {
    fun calculateProgress(completedCount: Int, totalCount: Int): Float {
        if (totalCount <= 0) return 0f
        return (completedCount.toFloat() / totalCount).coerceIn(0f, 1f)
    }

    fun calculatePercentage(completedCount: Int, totalCount: Int): Int {
        val progress = calculateProgress(completedCount, totalCount)
        return (progress * 100).toInt()
    }
}
