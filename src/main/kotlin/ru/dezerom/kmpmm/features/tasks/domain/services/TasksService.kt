package ru.dezerom.kmpmm.features.tasks.domain.services

import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.utils.security.*
import ru.dezerom.kmpmm.common.utils.toUUID
import ru.dezerom.kmpmm.features.tasks.data.repository.TaskRepository
import ru.dezerom.kmpmm.features.tasks.domain.mappers.toDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.changeComplete.ChangeCompleteDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.edit.EditTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto

class TasksService(
    private val repository: TaskRepository
) {
    suspend fun createTask(principle: UserIdPrinciple?, task: CreateTaskDto?): Result<GetTaskDto> {
        if (principle == null) return defaultAuthError()

        if (task == null || task.name.isNullOrBlank()) {
            return defaultNoDataError()
        }

        return repository.createTask(
            userId = principle.id,
            name = task.name,
            description = task.description,
            deadline = task.deadline
        ).map { it.toDto() }
    }

    suspend fun editTask(principle: UserIdPrinciple?, task: EditTaskDto?): Result<GetTaskDto> {
        if (principle == null) return defaultAuthError()

        if (task == null || task.id.isNullOrBlank() || task.name.isNullOrBlank()) {
            return defaultNoDataError()
        }

        val taskUUID = task.id.toUUID() ?: return wrongDataFormatError()

        val foundTask = repository.getTask(taskUUID).fold(
            onSuccess = { it },
            onFailure = { return notFoundError() }
        )

        if (foundTask.creatorId != principle.id) return authError()

        return repository.editTask(
            taskId = taskUUID,
            newName = task.name,
            newDescription = task.description,
            newDeadline = task.deadline
        ).fold(
            onSuccess = {
                if (it)
                    Result.success(
                        foundTask.copy(
                            name = task.name,
                            description = task.description,
                            deadline = task.deadline
                        ).toDto()
                    )
                else
                    unknownError()
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun changeCompleteStatus(principle: UserIdPrinciple?, taskId: String?): Result<ChangeCompleteDto> {
        if (principle == null) return defaultAuthError()
        if (taskId.isNullOrBlank()) return defaultNoDataError()

        val taskUUID = taskId.toUUID()?: return wrongDataFormatError()

        val foundTask = repository.getTask(taskUUID).fold(
            onSuccess = { it },
            onFailure = { return notFoundError() }
        )

        if (foundTask.creatorId != principle.id) return authError()
        val isCompleted = foundTask.isCompleted

        val time = if (isCompleted) null else System.currentTimeMillis()
        return repository.changeCompletedStatus(
            taskId = taskUUID,
            newStatus = !foundTask.isCompleted,
            time = time
        ).fold(
            onSuccess = {
                if (it) Result.success(
                    ChangeCompleteDto(
                        success = true,
                        completedAt = time
                    )
                ) else {
                    unknownError()
                }
            },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun getTasks(principle: UserIdPrinciple?): Result<GetTasksDto> {
        if (principle == null) return defaultAuthError()

        return repository.getTasks(principle.id).map { it.toDto() }
    }

    suspend fun deleteTask(principle: UserIdPrinciple?, taskId: String?): Result<BoolResponse> {
        if (principle == null) return defaultAuthError()
        if (taskId.isNullOrBlank()) return defaultNoDataError()

        val taskUUID = taskId.toUUID()?: return wrongDataFormatError()

        val foundTask = repository.getTask(taskUUID).fold(
            onSuccess = { it },
            onFailure = { return notFoundError() }
        )

        if (foundTask.creatorId!= principle.id) return authError()

        return repository.deleteTask(taskUUID)
    }
}
