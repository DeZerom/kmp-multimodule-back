package ru.dezerom.kmpmm.features.auth.data.repository

import ru.dezerom.kmpmm.features.auth.data.sources.UserSource

class AuthRepository(
    private val userSource: UserSource,
) {
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
