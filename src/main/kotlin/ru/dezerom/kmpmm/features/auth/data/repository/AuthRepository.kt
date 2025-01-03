package ru.dezerom.kmpmm.features.auth.data.repository

import ru.dezerom.kmpmm.features.auth.data.sources.TokenSource
import ru.dezerom.kmpmm.features.auth.data.sources.UserSource
import ru.dezerom.kmpmm.features.auth.domain.models.TokensModel
import ru.dezerom.kmpmm.features.auth.domain.models.UserModel
import java.util.*

class AuthRepository(
    private val userSource: UserSource,
    private val tokenSource: TokenSource,
) {
    suspend fun deleteTokens(tokensId: UUID): Result<Boolean> {
        return tokenSource.deleteTokens(tokensId).map { it >= 0 }
    }

    suspend fun getTokensId(userId: UUID, refreshToken: String): Result<UUID?> {
        return tokenSource.getTokenByRefreshToken(userId, refreshToken).map { it?.id }
    }

    suspend fun checkToken(userId: UUID, token: String): Result<Boolean> {
        return tokenSource.getToken(userId, token).map { it != null }
    }

    suspend fun saveTokens(
        userId: UUID,
        accessToken: String,
        refreshToken: String,
    ): Result<TokensModel> {
        return tokenSource.saveTokens(userId, accessToken, refreshToken)
    }

    suspend fun getUserById(userId: UUID): Result<UserModel> {
        return userSource.getUser(userId)
    }

    suspend fun getUserByLogin(login: String): Result<UserModel?> {
        return userSource.getUser(login)
    }

    suspend fun hasUser(login: String): Result<Boolean> {
        return userSource.getUser(login).map { it != null }
    }

    suspend fun registerUser(
        login: String,
        passHash: String
    ): Result<Boolean> {
        return userSource.addUser(
            newLogin = login,
            newPasswordHash = passHash
        ).map { true }
    }
}
