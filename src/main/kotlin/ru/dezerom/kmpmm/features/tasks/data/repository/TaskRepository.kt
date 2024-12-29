package ru.dezerom.kmpmm.features.tasks.data.repository

import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.tasks.data.sources.TaskSource
import ru.dezerom.kmpmm.features.tasks.domain.mappers.toDomain
import ru.dezerom.kmpmm.features.tasks.domain.models.TaskModel
import java.util.*

class TaskRepository(
    private val source: TaskSource
) {
    suspend fun createTask(
        userId: UUID,
        name: String,
        description: String?,
        deadline: Long?
    ): Result<BoolResponse> {
        return source.createTask(userId, name, description, deadline).map { BoolResponse(true) }
    }

    suspend fun editTask(
        taskId: UUID,
        newName: String,
        newDescription: String?,
        newDeadline: Long?,
    ): Result<BoolResponse> {
        return source.editTask(taskId, newName, newDescription, newDeadline).map { BoolResponse(it) }
    }

    suspend fun changeCompletedStatus(
        taskId: UUID,
        newStatus: Boolean,
        time: Long?
    ): Result<BoolResponse> {
        return source.changeCompletedStatus(taskId, newStatus, time).map { BoolResponse(it) }
    }

    suspend fun getTasks(userId: UUID): Result<List<TaskModel>> {
        return source.getTasks(userId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getTask(taskId: UUID): Result<TaskModel> {
        return source.getTask(taskId).map { it.toDomain() }
    }
}
