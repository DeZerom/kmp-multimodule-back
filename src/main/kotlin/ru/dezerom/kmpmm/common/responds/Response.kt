package ru.dezerom.kmpmm.common.responds

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val success: Boolean,
    val body: T,
    val error: String? = null
)
