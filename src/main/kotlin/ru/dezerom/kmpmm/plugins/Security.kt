package ru.dezerom.kmpmm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.Response

const val JWT_LOGIN_CLAIM = "login"
const val JWT_AUTH_TOKEN = "auth-token"

private const val AUTH_HEADER = "Authorization"

fun Application.configureSecurity() {
    authentication {
        jwt(JWT_AUTH_TOKEN) {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Secrets.JWT_SECRET))
                    .withIssuer(Secrets.JWT_ISSUER)
                    .build()
            )

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = Response(
                        success = false,
                        body = StringConst.Errors.AUTH_ERROR
                    )
                )
            }
        }
    }
}
