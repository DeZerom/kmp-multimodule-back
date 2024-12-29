package ru.dezerom.kmpmm.features.tasks.routing.dto.get

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTaskDto(
    val id: String,
    val name: String,
    val description: String?,
    val deadline: Long?,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("is_completed") val isCompleted: Boolean,
    @SerialName("completed_at") val completedAt: Long?
)
