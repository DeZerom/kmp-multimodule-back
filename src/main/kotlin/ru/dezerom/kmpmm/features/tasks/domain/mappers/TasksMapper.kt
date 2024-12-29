package ru.dezerom.kmpmm.features.tasks.domain.mappers

import ru.dezerom.kmpmm.features.tasks.data.tables.TaskDao
import ru.dezerom.kmpmm.features.tasks.domain.models.TaskModel
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto

fun List<TaskModel>.toDto() = GetTasksDto(
    tasks = map { it.toDto() }
)

fun TaskModel.toDto() = GetTaskDto(
    id = id.toString(),
    name = name,
    description = description,
    deadline = deadline,
    createdAt = createdAt,
    isCompleted = isCompleted,
    completedAt = completedAt
)

fun TaskDao.toDomain() = TaskModel(
    id = id.value,
    name = title,
    description = description,
    deadline = deadline,
    creatorId = userId.value,
    createdAt = createdAt,
    isCompleted = isCompleted,
    completedAt = completedAt
)
