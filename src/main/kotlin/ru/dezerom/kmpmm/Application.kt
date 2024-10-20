package ru.dezerom.kmpmm

import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.dezerom.kmpmm.plugins.*

fun main() {
    embeddedServer(
        Netty,
        environment = applicationEngineEnvironment {
            config = MapApplicationConfig(
                ConfigVariables.ACCESS_TOKEN_TIMEOUT to "60000", //minute
                ConfigVariables.REFRESH_TOKEN_TIMEOUT to "86400000" //24 hours
            )

            connector {
                connector {
                    port = 8080
                    host = "0.0.0.0"
                }
            }

            module { module() }
        },
    ).start(wait = true)
}

fun Application.module() {
    configureDB()
    configureSecurity()
    configureSerialization()
    configureKoin()
    configureRouting()
}
