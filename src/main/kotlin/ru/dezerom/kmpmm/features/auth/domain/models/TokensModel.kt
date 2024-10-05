package ru.dezerom.kmpmm.features.auth.domain.models

import java.util.*

data class TokensModel(
    val id: UUID,
    val userId: UUID,
    val accessToken: String,
    val refreshToken: String
)
