package ru.dezerom.kmpmm.features.auth.data.repository

import ru.dezerom.kmpmm.features.auth.data.sources.UserSource
import ru.dezerom.kmpmm.features.auth.domain.models.UserModel

class AuthRepository(
    private val userSource: UserSource,
    private val tokenSource: TokenSource,
) {
    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ): Result<Boolean> {
        return tokenSource.saveTokens(accessToken, refreshToken)
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
