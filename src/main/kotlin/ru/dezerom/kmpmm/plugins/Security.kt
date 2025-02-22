package ru.dezerom.kmpmm.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import org.koin.ktor.ext.inject
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.utils.security.UserIdAndTokenIdPrinciple
import ru.dezerom.kmpmm.common.utils.security.UserIdPrinciple
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import java.util.*

const val JWT_ID_CLAIM = "user_id"
const val JWT_AUTH_TOKEN = "auth-token"
const val JWT_REFRESH_TOKEN = "refresh-token"
const val JWT_IS_REFRESH = "is_refresh"

private val logger = KtorSimpleLogger("AUTH-SECURITY_LOGGER")
private const val AUTH_HEADER = "Authorization"
private const val BEARER = "Bearer "

fun Application.configureSecurity() {
    val authRepository: AuthRepository by inject()

    authentication {
        jwt(JWT_AUTH_TOKEN) {
            verifier(getVerifier(false))

            validate { jwtCredential ->
                val id = getUserId(jwtCredential) ?: return@validate null
                val token = getToken(request) ?: return@validate null

                val uuidId = idToUUID(id) ?: return@validate null

                authRepository.checkToken(userId = uuidId, token = token).fold(
                    onSuccess = {
                        if (it)
                            return@validate UserIdPrinciple(uuidId)
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

            challenge(block = { _, _ -> defaultChallenge() })
        }

        jwt(JWT_REFRESH_TOKEN) {
            verifier(getVerifier(true))

            validate { jwtCredential ->
                val id = getUserId(jwtCredential) ?: return@validate null
                val token = getToken(request) ?: return@validate null

                val uuidId = idToUUID(id)?: return@validate null

                authRepository.getTokensId(uuidId, token).fold(
                    onSuccess = {
                        if (it != null) {
                            return@validate UserIdAndTokenIdPrinciple(uuidId, it)
                        } else {
                            logger.error("Wrong token")
                            return@validate null
                        }
                    },
                    onFailure = { return@validate null }
                )
            }

            challenge(block = { _, _ -> defaultChallenge() })
        }
    }
}

private fun getVerifier(isRefresh: Boolean) = JWT
    .require(Algorithm.HMAC256(Secrets.JWT_SECRET))
    .withIssuer(Secrets.JWT_ISSUER)
    .withClaim(JWT_IS_REFRESH, isRefresh)
    .build()

private fun getUserId(jwtCredential: JWTCredential): String? {
    return jwtCredential.getClaim(JWT_ID_CLAIM, String::class) ?: run {
        logger.error("No user id")
        return null
    }
}

private fun getToken(request: ApplicationRequest): String? {
    return request.headers[AUTH_HEADER]?.replace(BEARER, "") ?: run {
        logger.error("No token")
        return null
    }
}

private fun idToUUID(id: String): UUID? {
    return try {
        UUID.fromString(id)
    } catch (_: Exception) {
        logger.error("Invalid user id")
        return null
    }
}

private suspend fun JWTChallengeContext.defaultChallenge() {
    call.respond(
        status = HttpStatusCode.Unauthorized,
        message = Response(
            success = false,
            body = null,
            error = StringConst.Errors.AUTH_ERROR
        )
    )
}
