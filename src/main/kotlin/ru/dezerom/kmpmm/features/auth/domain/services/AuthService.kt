package ru.dezerom.kmpmm.features.auth.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import ru.dezerom.kmpmm.Config
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.security.UserIdAndTokenIdPrinciple
import ru.dezerom.kmpmm.common.utils.security.UserIdPrinciple
import ru.dezerom.kmpmm.common.utils.sha256Hash
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.domain.mapper.toDto
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.auth.routing.dto.UserDto
import ru.dezerom.kmpmm.features.tasks.data.repository.TaskRepository
import ru.dezerom.kmpmm.plugins.JWT_ID_CLAIM
import ru.dezerom.kmpmm.plugins.JWT_IS_REFRESH
import java.util.*

class AuthService(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val config: Config
) {
    suspend fun refreshTokens(principle: UserIdAndTokenIdPrinciple?): Result<TokensDto> {
        if (principle == null) return Result.failure(
            ResponseError(StringConst.Errors.AUTH_ERROR, HttpStatusCode.Unauthorized)
        )

        authRepository.deleteTokens(principle.tokenId).fold(
            onSuccess = {},
            onFailure = { return Result.failure(it) }
        )

        return saveTokens(principle.userId)
    }

    suspend fun getUser(token: UserIdPrinciple?): Result<UserDto> {
        if (token == null) return Result.failure(
            ResponseError(StringConst.Errors.AUTH_ERROR, HttpStatusCode.Unauthorized)
        )

        val user = authRepository.getUserById(token.id).fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        val stats = taskRepository.getStats(user.id).fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        return Result.success(user.toDto(stats))
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

        return saveTokens(user.id)
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

    private suspend fun saveTokens(userId: UUID): Result<TokensDto> {
        val access = createJWT(userId.toString(), false)
        val refresh = createJWT(userId.toString(), true)

        return authRepository.saveTokens(
            userId = userId,
            accessToken = access,
            refreshToken = refresh
        ).fold(
            onSuccess = { Result.success(it.toDto()) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun createJWT(userId: String, isRefresh: Boolean): String {
        val liveLength = if (isRefresh)
            config.refreshTokenLiveLength
        else
            config.accessTokenLiveLength

        return JWT.create()
            .withIssuer(Secrets.JWT_ISSUER)
            .withClaim(JWT_ID_CLAIM, userId)
            .withClaim(JWT_IS_REFRESH, isRefresh)
            .withExpiresAt(Date(System.currentTimeMillis() + liveLength))
            .sign(Algorithm.HMAC256(Secrets.JWT_SECRET))
    }
}
