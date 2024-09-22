package ru.dezerom.kmpmm

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.dezerom.kmpmm.plugins.configureFrameworks
import ru.dezerom.kmpmm.plugins.configureRouting
import ru.dezerom.kmpmm.plugins.configureSecurity
import ru.dezerom.kmpmm.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureFrameworks()
    configureRouting()
}
