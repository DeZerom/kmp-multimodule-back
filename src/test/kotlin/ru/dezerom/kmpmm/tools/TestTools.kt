package ru.dezerom.kmpmm.tools

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import ru.dezerom.kmpmm.ConfigVariables
import ru.dezerom.kmpmm.plugins.*

const val TEST_ACCESS_TOKEN_TIMEOUT = 5000L

internal fun TestApplicationBuilder.createApp() {
    application {
        configureSecurity()
        configureRouting()
        configureSerialization()
        configureKoin(appConfig = config)
        configureDB(test = true)
    }
}

internal fun ApplicationTestBuilder.createCustomClient() = createClient {
    install(ContentNegotiation) {
        json()
    }
}

private val config = MapApplicationConfig(
    ConfigVariables.ACCESS_TOKEN_TIMEOUT to TEST_ACCESS_TOKEN_TIMEOUT.toString(), //second
    ConfigVariables.REFRESH_TOKEN_TIMEOUT to "86400000" //24 hours
)
