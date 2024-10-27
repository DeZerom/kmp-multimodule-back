package ru.dezerom.kmpmm.features.tasks.data.sources

import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.db.safeSuspendTransaction
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable
import ru.dezerom.kmpmm.features.tasks.data.tables.TaskDao
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
        }
    }
}
