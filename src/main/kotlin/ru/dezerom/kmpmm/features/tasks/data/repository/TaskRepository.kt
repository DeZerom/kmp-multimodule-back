package ru.dezerom.kmpmm.features.tasks.data.repository

import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.tasks.data.sources.TaskSource
import ru.dezerom.kmpmm.features.tasks.domain.mappers.toDomain
import ru.dezerom.kmpmm.features.tasks.domain.models.TaskModel
import ru.dezerom.kmpmm.features.tasks.domain.models.UserStats
import java.util.*

class TaskRepository(
    private val source: TaskSource
) {
    suspend fun createTask(
        userId: UUID,
        name: String,
        description: String?,
        deadline: Long?
    ): Result<TaskModel> {
        return source.createTask(userId, name, description, deadline).map { it.toDomain() }
    }

    suspend fun editTask(
        taskId: UUID,
        newName: String,
        newDescription: String?,
        newDeadline: Long?,
    ): Result<Boolean> {
        return source.editTask(taskId, newName, newDescription, newDeadline).map { it }
    }

    suspend fun changeCompletedStatus(
        taskId: UUID,
        newStatus: Boolean,
        time: Long?
    ): Result<Boolean> {
        return source.changeCompletedStatus(taskId, newStatus, time)
    }

    suspend fun getTasks(userId: UUID): Result<List<TaskModel>> {
        return source.getTasks(userId).map { list -> list.map { it.toDomain() } }
    }

    suspend fun getTask(taskId: UUID): Result<TaskModel> {
        return source.getTask(taskId).map { it.toDomain() }
    }

    suspend fun deleteTask(taskId: UUID): Result<BoolResponse> {
        return source.deleteTask(taskId).map { BoolResponse(it) }
    }

    suspend fun getStats(userId: UUID): Result<UserStats> {
        return source.getTasks(userId).map {
            val completedTasks = it.filter { task -> task.isCompleted }.size
            UserStats(
                userId = userId,
                tasks = it.size,
                completedTasks = completedTasks,
                uncompletedTasks = it.size - completedTasks
            )
        }
    }
}
