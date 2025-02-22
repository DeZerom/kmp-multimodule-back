package ru.dezerom.kmpmm.common.responds.errors

import ru.dezerom.kmpmm.common.responds.Response

fun ResponseError.toResponse(): Response<String?> = Response(
    success = false,
    body = null,
    error = message ?: "",
)
