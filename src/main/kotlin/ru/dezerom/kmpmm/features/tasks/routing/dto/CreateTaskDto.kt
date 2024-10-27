package ru.dezerom.kmpmm.features.tasks.routing.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskDto(
    val name: String? = null,
    val description: String? = null,
    val deadline: Long? = null
)
