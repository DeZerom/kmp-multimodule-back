package ru.dezerom.kmpmm.features.auth.routing.dto

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsDto(
    val login: String?,
    val password: String?,
)
