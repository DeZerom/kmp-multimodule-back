package ru.dezerom.kmpmm.common.responds

import kotlinx.serialization.Serializable

@Serializable
data class StringResponse(
    val response: String
)
