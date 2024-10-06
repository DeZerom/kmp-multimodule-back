package ru.dezerom.kmpmm.features.auth.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.sha256Hash
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.domain.mapper.toDto
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.auth.routing.dto.UserDto
import ru.dezerom.kmpmm.plugins.JWT_LOGIN_CLAIM
import java.util.*

class AuthService(
    private val authRepository: AuthRepository,
) {
    suspend fun getUser(token: String?): Result<UserDto> {
        val login = token?.getClaim(JWT_LOGIN_CLAIM, String::class) ?: return  Result.failure(
            ResponseError(StringConst.Errors.NO_USER, HttpStatusCode.BadRequest)
        )

        val user = authRepository.getUserByLogin(login).fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        if (user == null) {
            return Result.failure(
                ResponseError(StringConst.Errors.NO_USER, HttpStatusCode.BadRequest)
            )
        }

        authRepository.checkToken(user.id, "token.").fold(
            onSuccess = {
                if (it) {
                    return Result.success(user.toDto())
                } else {
                    return Result.failure(
                        ResponseError(StringConst.Errors.AUTH_ERROR, HttpStatusCode.Unauthorized)
                    )
                }
            },
            onFailure = { return Result.failure(it) }
        )
    }

    suspend fun authorizeUser(credentials: CredentialsDto?): Result<TokensDto> {
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

        return authRepository.saveTokens(
            userId = user.id,
            accessToken = access,
            refreshToken = refresh
        ).fold(
            onSuccess = { Result.success(it.toDto()) },
            onFailure = { Result.failure(it) }
        )
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
            .withIssuer(Secrets.JWT_ISSUER)
            .withClaim(JWT_LOGIN_CLAIM, credentials.login)
            .withExpiresAt(Date(System.currentTimeMillis() + liveLength))
            .sign(Algorithm.HMAC256(Secrets.JWT_SECRET))
    }
}
