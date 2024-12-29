package ru.dezerom.kmpmm.features.auth.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.utils.makeResponse
import ru.dezerom.kmpmm.features.auth.domain.services.AuthService
import ru.dezerom.kmpmm.plugins.JWT_AUTH_TOKEN
import ru.dezerom.kmpmm.plugins.JWT_REFRESH_TOKEN

fun Application.configureAuthRoutes() {
    val authService: AuthService by inject()

    routing {
        post(Urls.Auth.REGISTER) {
            call.makeResponse { authService.registerUser(call.receiveNullable()) }
        }

        post(Urls.Auth.AUTHORIZE) {
            call.makeResponse { authService.authorizeUser(call.receiveNullable()) }
        }

        authenticate(JWT_REFRESH_TOKEN) {
            post(Urls.Auth.REFRESH) {
                call.makeResponse { authService.refreshTokens(call.principal()) }
            }
        }

        authenticate(JWT_AUTH_TOKEN) {
            get(Urls.Auth.ME) {
                call.makeResponse { authService.getUser(call.principal()) }
            }
        }

    }
}
