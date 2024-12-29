package ru.dezerom.kmpmm.features.tasks.routing.dto.get

import kotlinx.serialization.Serializable

@Serializable
data class GetTaskDto(
    val id: String,
    val name: String,
    val description: String?,
    val deadline: Long?,
)
