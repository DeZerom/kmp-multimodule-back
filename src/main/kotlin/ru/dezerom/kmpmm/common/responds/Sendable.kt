package ru.dezerom.kmpmm.common.responds

interface Sendable<T> {
    fun toDto(): T
}
