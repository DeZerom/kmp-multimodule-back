package ru.dezerom.kmpmm.features.tasks.domain.models

import java.util.*

data class TaskModel(
    val id: UUID,
    val name: String,
    val description: String?,
    val deadline: Long?,
    val creatorId: UUID,
)
