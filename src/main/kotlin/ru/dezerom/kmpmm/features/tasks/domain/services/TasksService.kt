package ru.dezerom.kmpmm.features.tasks.domain.services

import io.ktor.http.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.security.UserIdPrinciple
import ru.dezerom.kmpmm.common.utils.security.defaultAuthError
import ru.dezerom.kmpmm.features.tasks.data.repository.TaskRepository
import ru.dezerom.kmpmm.features.tasks.routing.dto.CreateTaskDto

class TasksService(
    private val repository: TaskRepository
) {
    suspend fun createTask(principle: UserIdPrinciple?, task: CreateTaskDto?): Result<BoolResponse> {
        if (principle == null) return defaultAuthError()

        if (task == null || task.name.isNullOrBlank()) {
            return Result.failure(
                ResponseError(
                    message = StringConst.Errors.NO_DATA,
                    code = HttpStatusCode.BadRequest
                )
            )
        }

        return repository.createTask(
            userId = principle.id,
            name = task.name,
            description = task.description,
            deadline = task.deadline
        )
    }
}
