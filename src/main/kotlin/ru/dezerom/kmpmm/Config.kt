package ru.dezerom.kmpmm

import io.ktor.server.config.*

class Config(
    private val appConfig: ApplicationConfig
) {
    val accessTokenLiveLength
        get() = appConfig.propertyOrNull(ConfigVariables.ACCESS_TOKEN_TIMEOUT)?.getString()?.toLongOrNull() ?: 60000

    val refreshTokenLiveLength
        get() = appConfig.propertyOrNull(ConfigVariables.REFRESH_TOKEN_TIMEOUT)?.getString()?.toLongOrNull()
            ?: 86400000
}
