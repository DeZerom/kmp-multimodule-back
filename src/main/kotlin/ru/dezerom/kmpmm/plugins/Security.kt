package ru.dezerom.kmpmm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.utils.security.UUIDPrinciple
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import java.util.*

const val JWT_ID_CLAIM = "user_id"
const val JWT_AUTH_TOKEN = "auth-token"

private val logger = KtorSimpleLogger("AUTH-SECURITY_LOGGER")
private const val AUTH_HEADER = "Authorization"
private const val BEARER = "Bearer "

fun Application.configureSecurity() {
    val authRepository: AuthRepository by inject()

    authentication {
        jwt(JWT_AUTH_TOKEN) {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Secrets.JWT_SECRET))
                    .withIssuer(Secrets.JWT_ISSUER)
                    .build()
            )

            validate { jwtCredential ->
                val id = jwtCredential.getClaim(JWT_ID_CLAIM, String::class) ?: run {
                    logger.error("No user id")
                    return@validate null
                }
                val token = request.headers[AUTH_HEADER]?.replace(BEARER, "") ?: return@validate null

                val uuidId = try {
                    UUID.fromString(id)
                } catch (_: Exception) {
                    logger.error("Invalid user id")
                    return@validate null
                }

                authRepository.checkToken(userId = UUID.fromString(id), token = token).fold(
                    onSuccess = {
                        if (it)
                            return@validate UUIDPrinciple(uuidId)
                        else {
                            logger.error("Wrong token")
                            return@validate null
                        }
                    },
                    onFailure = {
                        logger.error(it)
                        return@validate null
                    }
                )
            }

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
