package ru.dezerom.kmpmm.features.auth.data.sources

import io.ktor.http.*
import org.jetbrains.exposed.dao.id.EntityID
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.db.safeSuspendTransaction
import ru.dezerom.kmpmm.features.auth.data.tables.TokenDao
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable
import ru.dezerom.kmpmm.features.auth.domain.mapper.toDomain
import java.util.*

class TokenSource {
    suspend fun saveTokens(userId: UUID, accessToken: String, refreshToken: String) =
        safeSuspendTransaction(
            errorMessage = StringConst.Errors.AUTH_ERROR,
            errorCode = HttpStatusCode.InternalServerError
        ) {
            TokenDao.new {
                this.userId = EntityID(userId, UserTable)
                this.accessToken = accessToken
                this.refreshToken = refreshToken
            }.toDomain()
        }
}
