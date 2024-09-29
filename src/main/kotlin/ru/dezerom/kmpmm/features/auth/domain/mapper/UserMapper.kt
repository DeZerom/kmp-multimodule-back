package ru.dezerom.kmpmm.features.auth.domain.mapper

import ru.dezerom.kmpmm.features.auth.data.tables.UserDao
import ru.dezerom.kmpmm.features.auth.domain.models.UserModel

fun UserDao.toDomain() = UserModel(
    id = id.value,
    login = login,
    password = password
)
