package ru.dezerom.kmpmm.common.db

import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.dezerom.kmpmm.common.responds.errors.ResponseError

private val logger = KtorSimpleLogger("DbActionsLogger")

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

suspend fun <T> safeSuspendTransaction(
    errorMessage: String,
    errorCode: HttpStatusCode,
    block: Transaction.() -> T
): Result<T> =
    try {
        Result.success(suspendTransaction(block))
    } catch (e: Exception) {
        logger.error(e)
        Result.failure(ResponseError(code = errorCode, message = errorMessage))
    }