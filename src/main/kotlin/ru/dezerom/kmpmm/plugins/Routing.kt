package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import ru.dezerom.kmpmm.features.auth.routing.configureAuthRoutes
import ru.dezerom.kmpmm.features.tasks.routing.configureTasksRouting

fun Application.configureRouting() {
    configureAuthRoutes()
    configureTasksRouting()
}
