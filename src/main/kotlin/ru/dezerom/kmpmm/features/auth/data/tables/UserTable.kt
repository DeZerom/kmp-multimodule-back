package ru.dezerom.kmpmm.features.auth.data.tables

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserTable: UUIDTable() {
    val login = varchar("login", 255).uniqueIndex()
    val password = varchar("pass", 64)
}

class UserDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(UserTable)

    var login by UserTable.login
    var password by UserTable.password
}
