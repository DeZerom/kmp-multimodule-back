package ru.dezerom.kmpmm.common.responds.errors

import io.ktor.http.*

class ResponseError(
    message: String,
    val code: HttpStatusCode
): Exception(message)
