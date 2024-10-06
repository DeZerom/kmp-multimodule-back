package ru.dezerom.kmpmm.features.auth.routing.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val login: String
)
