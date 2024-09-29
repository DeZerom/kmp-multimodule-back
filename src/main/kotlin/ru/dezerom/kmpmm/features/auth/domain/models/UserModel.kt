package ru.dezerom.kmpmm.features.auth.domain.models

import java.util.*

data class UserModel(
    val id: UUID,
    val login: String,
    val password: String
)
