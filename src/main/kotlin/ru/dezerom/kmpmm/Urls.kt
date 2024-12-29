package ru.dezerom.kmpmm

internal object Urls {
    object Auth {
        private const val PREFIX = "/auth/"

        const val REGISTER = "${PREFIX}register"
        const val AUTHORIZE = "${PREFIX}auth"
        const val ME = "${PREFIX}me"
        const val REFRESH = "${PREFIX}refresh"
    }

    object Tasks {
        private const val PREFIX = "/tasks/"

        const val CREATE = "${PREFIX}create"
        const val EDIT = "${PREFIX}edit"
        const val GET_ALL = "${PREFIX}all"
    }
}
