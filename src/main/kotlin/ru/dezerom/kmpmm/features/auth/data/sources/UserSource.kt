package ru.dezerom.kmpmm.features.auth.data.sources

import io.ktor.http.*
import ru.dezerom.kmpmm.common.db.safeSuspendTransaction
import ru.dezerom.kmpmm.features.auth.data.tables.UserDao
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable
import ru.dezerom.kmpmm.features.auth.domain.mapper.toDomain
import ru.dezerom.kmpmm.features.auth.domain.models.UserModel

class UserSource {

    suspend fun getUser(login: String): Result<UserModel?> = safeSuspendTransaction(
        errorMessage = "Ошибка при получении пользователя",
        errorCode = HttpStatusCode.InternalServerError,
    ) {
        UserDao.find {
            UserTable.login eq login
        }.firstOrNull()?.toDomain()
    }

    suspend fun addUser(
        newLogin: String,
        newPasswordHash: String
    ) = safeSuspendTransaction(
        errorMessage = "Ошибка при создании пользователя",
        errorCode = HttpStatusCode.BadRequest
    ) {
        UserDao.new {
            login = newLogin
            password = newPasswordHash
        }
    }

}
