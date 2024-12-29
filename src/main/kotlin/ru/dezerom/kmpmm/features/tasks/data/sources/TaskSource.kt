package ru.dezerom.kmpmm.features.tasks.data.sources

import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.update
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.db.safeSuspendTransaction
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable
import ru.dezerom.kmpmm.features.tasks.data.tables.TaskDao
import ru.dezerom.kmpmm.features.tasks.data.tables.TaskTable
import java.util.*

class TaskSource {
    suspend fun createTask(
        userId: UUID,
        name: String,
        description: String?,
        deadline: Long?
    ): Result<TaskDao> = safeSuspendTransaction(
        errorCode = HttpStatusCode.InternalServerError,
        errorMessage = StringConst.Errors.INTERNAL_ERROR
    ) {
        TaskDao.new {
            this.userId = EntityID(userId, UserTable)
            title = name
            this.description = description
            this.deadline = deadline
            isCompleted = false
            completedAt = null
            createdAt = System.currentTimeMillis()
        }
    }

    suspend fun editTask(
        taskId: UUID,
        newName: String,
        newDescription: String?,
        newDeadline: Long?
    ): Result<Boolean> = safeSuspendTransaction(
        errorCode = HttpStatusCode.InternalServerError,
        errorMessage = StringConst.Errors.INTERNAL_ERROR,
    ) {
        TaskTable.update(where = { TaskTable.id eq taskId }) {
            it[title] = newName
            it[description] = newDescription
            it[deadline] = newDeadline
        } > 0
    }

    suspend fun changeCompletedStatus(
        taskId: UUID,
        newStatus: Boolean,
        newCompletedAt: Long?
    ): Result<Boolean> = safeSuspendTransaction(
        errorCode = HttpStatusCode.InternalServerError,
        errorMessage = StringConst.Errors.INTERNAL_ERROR,
    ) {
        TaskTable.update(where = { TaskTable.id eq taskId }) {
            it[isCompleted] = newStatus
            it[completedAt] = newCompletedAt
        } > 0
    }

    suspend fun getTasks(userId: UUID): Result<List<TaskDao>> = safeSuspendTransaction(
        errorCode = HttpStatusCode.InternalServerError,
        errorMessage = StringConst.Errors.INTERNAL_ERROR
    ) {
        TaskDao.find { TaskTable.userId eq userId }.sortedBy { it.createdAt }
    }

    suspend fun getTask(taskId: UUID): Result<TaskDao> = safeSuspendTransaction(
        errorCode = HttpStatusCode.InternalServerError,
        errorMessage = StringConst.Errors.INTERNAL_ERROR
    ) {
        TaskDao[taskId]
    }
}
