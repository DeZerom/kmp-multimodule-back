package ru.dezerom.kmpmm.features.auth.domain.mapper

import ru.dezerom.kmpmm.features.auth.data.tables.UserDao
import ru.dezerom.kmpmm.features.auth.domain.models.UserModel
import ru.dezerom.kmpmm.features.auth.routing.dto.UserDto
import ru.dezerom.kmpmm.features.tasks.domain.models.UserStats

fun UserDao.toDomain() = UserModel(
    id = id.value,
    login = login,
    password = password
)

fun UserModel.toDto(stats: UserStats) = UserDto(
    id = id.toString(),
    login = login,
    tasks = stats.tasks,
    completedTasks = stats.completedTasks,
    uncompletedTasks = stats.uncompletedTasks
)
