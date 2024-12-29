package ru.dezerom.kmpmm.features.tasks.domain.services

import io.ktor.http.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.security.UserIdPrinciple
import ru.dezerom.kmpmm.common.utils.security.defaultAuthError
import ru.dezerom.kmpmm.features.tasks.data.repository.TaskRepository
import ru.dezerom.kmpmm.features.tasks.domain.mappers.toDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto

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

    suspend fun getTasks(principle: UserIdPrinciple?): Result<GetTasksDto> {
        if (principle == null) return defaultAuthError()

        return repository.getTasks(principle.id).map { it.toDto() }
    }
}
