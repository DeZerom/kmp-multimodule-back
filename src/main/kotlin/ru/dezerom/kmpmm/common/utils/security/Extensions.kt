package ru.dezerom.kmpmm.common.utils.security

import io.ktor.http.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.errors.ResponseError

fun <T> defaultAuthError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.AUTH_ERROR,
    code = HttpStatusCode.Unauthorized
))

inline fun UserIdPrinciple?.ifNull(block: () -> Unit) {
    if (this == null) block()
}