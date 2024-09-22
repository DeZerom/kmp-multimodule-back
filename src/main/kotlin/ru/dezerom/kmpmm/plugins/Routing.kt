package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import ru.dezerom.kmpmm.features.auth.routing.configureAuthRoutes

fun Application.configureRouting() {
    configureAuthRoutes()
}
