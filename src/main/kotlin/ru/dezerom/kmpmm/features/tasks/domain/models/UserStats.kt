package ru.dezerom.kmpmm.features.tasks.domain.models

import java.util.*

data class UserStats(
    val userId: UUID,
    val tasks: Int,
    val completedTasks: Int,
    val uncompletedTasks: Int
)
