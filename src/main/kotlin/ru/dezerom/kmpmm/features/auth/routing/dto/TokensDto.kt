package ru.dezerom.kmpmm.features.auth.routing.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(
    val accessToken: String,
    val refreshToken: String
)
