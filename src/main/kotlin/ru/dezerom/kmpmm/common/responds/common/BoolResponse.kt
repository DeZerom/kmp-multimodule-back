package ru.dezerom.kmpmm.common.responds.common

import kotlinx.serialization.Serializable
import ru.dezerom.kmpmm.common.responds.Sendable

@Serializable
data class BoolResponse(
    val response: Boolean
): Sendable<BoolResponse> {
    override fun toDto(): BoolResponse = this
}
