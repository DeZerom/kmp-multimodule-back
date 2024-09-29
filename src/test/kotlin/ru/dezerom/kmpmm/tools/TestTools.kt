package ru.dezerom.kmpmm.tools

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import ru.dezerom.kmpmm.plugins.configureDB
import ru.dezerom.kmpmm.plugins.configureKoin
import ru.dezerom.kmpmm.plugins.configureRouting
import ru.dezerom.kmpmm.plugins.configureSerialization

internal fun TestApplicationBuilder.createApp() {
    application {
        configureRouting()
        configureSerialization()
        configureKoin()
        configureDB(test = true)
    }
}

internal fun ApplicationTestBuilder.createCustomClient() = createClient {
    install(ContentNegotiation) {
        json()
    }
}
