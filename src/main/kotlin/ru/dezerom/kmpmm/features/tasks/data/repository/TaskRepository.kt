package ru.dezerom.kmpmm.features.tasks.data.repository

import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.tasks.data.sources.TaskSource
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
}
