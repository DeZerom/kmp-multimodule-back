package ru.dezerom.kmpmm.features.tasks.routing.dto.get

import kotlinx.serialization.Serializable

@Serializable
data class GetTasksDto(
    val tasks: List<GetTaskDto>
)
