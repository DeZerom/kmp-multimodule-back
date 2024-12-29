package ru.dezerom.kmpmm.features.tasks.routing.dto.edit

import kotlinx.serialization.Serializable

@Serializable
data class EditTaskDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val deadline: Long? = null
)
