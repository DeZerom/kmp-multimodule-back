package ru.dezerom.kmpmm.common.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.Sendable
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.responds.errors.toResponse

suspend inline fun <reified R : Any, T: Sendable<R>> ApplicationCall.makeResponse(block: () -> Result<T>) {
    block().fold(
        onSuccess = { respond(Response(true, it.toDto())) },
        onFailure = {
            val err = it as? ResponseError

            respond(
                status = err?.code ?: HttpStatusCode.InternalServerError,
                message = err?.toResponse() ?: Response(false, "Что-то пошло не так")
            )
        }
    )
}
