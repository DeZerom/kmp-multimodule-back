package ru.dezerom.kmpmm.features.auth.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.sha256Hash
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import java.util.*

class AuthService(
    private val authRepository: AuthRepository,
) {
    suspend fun authorizeUser(credentials: CredentialsDto?): Result<String> {
        if (credentials == null || credentials.login.isNullOrBlank() || credentials.password.isNullOrBlank()) {
            return Result.failure(ResponseError(
                code = HttpStatusCode.BadRequest,
                message = StringConst.Errors.NO_CREDENTIALS
            ))
        }

        val user = authRepository.getUserByLogin(credentials.login).fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        if (user == null || user.password != sha256Hash(credentials.password)) {
            return Result.failure(ResponseError(
                code = HttpStatusCode.Unauthorized,
                message = StringConst.Errors.WRONG_CREDENTIALS
            ))
        }

        val access = createJWT(credentials, 60000)
        val refresh = createJWT(credentials, 60000 * 60 * 24)


    }

    suspend fun registerUser(credentials: CredentialsDto?): Result<BoolResponse> {
        if (credentials == null || credentials.login.isNullOrBlank() || credentials.password.isNullOrBlank()) {
            return Result.failure(ResponseError(
                code = HttpStatusCode.BadRequest,
                message = StringConst.Errors.NO_CREDENTIALS
            ))
        }

        val hasUserResponse = authRepository.hasUser(credentials.login)
        hasUserResponse.fold(
            onSuccess = {
                if (it) {
                    return Result.failure(ResponseError(
                        code = HttpStatusCode.BadRequest,
                        message = StringConst.Errors.USER_ALREADY_EXISTS
                    ))
                }
            },
            onFailure = { return Result.failure(it) }
        )

        val res = authRepository.registerUser(
            login = credentials.login,
            passHash = sha256Hash(credentials.password)
        )

        return res.fold(
            onSuccess = { Result.success(BoolResponse(it)) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun createJWT(credentials: CredentialsDto, liveLength: Long): String {
        return JWT.create()
            .withIssuer("/auth")
            .withClaim("login", credentials.login)
            .withExpiresAt(Date(System.currentTimeMillis() + liveLength))
            .sign(Algorithm.HMAC256(Secrets.JWT_SECRET))
    }
}
