package ru.dezerom.kmpmm.features.auth.routing

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.dezerom.kmpmm.common.utils.makeResponse
import ru.dezerom.kmpmm.features.auth.domain.services.AuthService

fun Application.configureAuthRoutes() {
    val authService: AuthService by inject()

    routing {
        post("/register") {
            call.makeResponse { authService.registerUser(call.receiveNullable()) }
        }

        post("/auth") {
            call.makeResponse { authService. }
        }
    }
}
