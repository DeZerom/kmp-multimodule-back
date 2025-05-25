package ru.dezerom.kmpmm.features.tasks.routing.dto.changeComplete

import kotlinx.serialization.Serializable

@Serializable
data class ChangeCompleteDto(
    val success: Boolean,
    val completedAt: Long?
)
