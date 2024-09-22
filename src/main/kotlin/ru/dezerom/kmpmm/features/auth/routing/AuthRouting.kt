package ru.dezerom.kmpmm.features.auth.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dezerom.kmpmm.common.responds.StringResponse

fun Application.configureAuthRoutes() {
    routing {
        post("/register") {
            call.respond(StringResponse("Hello, World!"))
        }
    }
}
