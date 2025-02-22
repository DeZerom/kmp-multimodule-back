package ru.dezerom.kmpmm.common.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.errors.ResponseError
import ru.dezerom.kmpmm.common.responds.errors.toResponse

suspend inline fun <reified R : Any> ApplicationCall.makeResponse(block: () -> Result<R>) {
    block().fold(
        onSuccess = { respond(Response(true, it)) },
        onFailure = {
            val err = it as? ResponseError

            respond(
                status = err?.code ?: HttpStatusCode.InternalServerError,
                message = err?.toResponse() ?: Response(false, null, "Что-то пошло не так")
            )
        }
    )
}
