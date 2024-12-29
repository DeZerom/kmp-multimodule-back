package ru.dezerom.kmpmm.features.tasks.routing.dto.edit

import kotlinx.serialization.Serializable

@Serializable
data class EditTaskDto(
    val id: String?,
    val name: String?,
    val description: String?,
    val deadline: Long?
)
