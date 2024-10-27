package ru.dezerom.kmpmm.common.constants

object StringConst {
    object Errors {
        const val NO_CREDENTIALS = "Не переданы логин и/или пароль"
        const val USER_ALREADY_EXISTS = "Пользователь с таким логином уже зарегистрирован"
        const val CANNOT_GET_USER = "Ошибка при получении пользователя"
        const val CANNOT_CREATE_USER = "Ошибка при создании пользователя"
        const val WRONG_CREDENTIALS = "Неверный логин или пароль"
        const val AUTH_ERROR = "Ошибка авторизации"
        const val NO_USER = "Такого пользователя не существует"
        const val INTERNAL_ERROR = "Внутренняя ошибка"

        const val NO_DATA = "Данные не переданы"
    }
}
