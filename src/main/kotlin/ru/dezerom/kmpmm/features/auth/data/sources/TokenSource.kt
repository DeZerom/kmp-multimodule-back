package ru.dezerom.kmpmm.features.auth.data.sources

class TokenSource {
    suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Boolean> {

    }
}
