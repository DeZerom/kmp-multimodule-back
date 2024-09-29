package ru.dezerom.kmpmm.common.constants

object StringConst {
    object Errors {
        const val NO_CREDENTIALS = "Не переданы логин и/или пароль"
        const val USER_ALREADY_EXISTS = "Пользователь с таким логином уже зарегистрирован"
        const val CANNOT_GET_USER = "Ошибка при получении пользователя"
        const val CANNOT_CREATE_USER = "Ошибка при создании пользователя"
        const val WRONG_CREDENTIALS = "Неверный логин или пароль"
    }
}
