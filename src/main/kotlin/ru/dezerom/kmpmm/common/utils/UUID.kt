package ru.dezerom.kmpmm.common.utils

import java.util.*

fun String.toUUID(): UUID? = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}