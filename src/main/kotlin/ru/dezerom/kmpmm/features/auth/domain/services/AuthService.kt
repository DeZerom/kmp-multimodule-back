package ru.dezerom.kmpmm.features.auth.domain.services

import io.ktor.http.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.utils.sha256Hash
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto

class AuthService(
    private val authRepository: AuthRepository
) {
    suspend fun registerUser(credentials: CredentialsDto?): Result<BoolResponse> {
        if (credentials == null || credentials.login.isNullOrBlank() || credentials.password.isNullOrBlank()) {
            return Result.failure(ResponseError(
                code = HttpStatusCode.BadRequest,
                message = StringConst.Errors.NO_CREDENTIALS
            ))
        }

        val  hasUserResponse = authRepository.hasUser(credentials.login)
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
}
