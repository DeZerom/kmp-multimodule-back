package ru.dezerom.kmpmm.common.utils.security

import io.ktor.http.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.errors.ResponseError

fun <T> defaultAuthError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.AUTH_ERROR,
    code = HttpStatusCode.Unauthorized
))

fun <T> defaultNoDataError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.NO_DATA,
    code = HttpStatusCode.BadRequest
))

fun <T> wrongDataFormatError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.WRONG_DATA_FORMAT,
    code = HttpStatusCode.BadRequest
))

fun <T> notFoundError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.NO_SUCH_DATA,
    code = HttpStatusCode.NotFound
))

fun <T> authError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.AUTH_ERROR,
    code = HttpStatusCode.Forbidden
))

fun <T> unknownError() = Result.failure<T>(ResponseError(
    message = StringConst.Errors.INTERNAL_ERROR,
    code = HttpStatusCode.InternalServerError
))

inline fun UserIdPrinciple?.ifNull(block: () -> Unit) {
    if (this == null) block()
}