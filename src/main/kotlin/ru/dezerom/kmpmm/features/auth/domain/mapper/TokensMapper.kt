package ru.dezerom.kmpmm.features.auth.domain.mapper

import ru.dezerom.kmpmm.features.auth.data.tables.TokenDao
import ru.dezerom.kmpmm.features.auth.domain.models.TokensModel
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto

fun TokenDao.toDomain() = TokensModel(
    id = id.value,
    userId = id.value,
    accessToken = accessToken,
    refreshToken = refreshToken
)

fun TokensModel.toDto() = TokensDto(
    accessToken = accessToken,
    refreshToken = refreshToken
)
