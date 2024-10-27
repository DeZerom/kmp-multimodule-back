package ru.dezerom.kmpmm.tools

internal object Urls {
    const val REG = "register"
    const val AUTH = "auth"
    const val ME = "me"
    const val REFRESH = "refresh"

    object Tasks {
        private const val PREFIX = "/tasks/"

        const val CREATE = "${PREFIX}create"
    }
}
