package ru.dezerom.kmpmm.features.auth.data.tables

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object TokenTable : UUIDTable() {
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val accessToken = text("access_token")
    val refreshToken = text("refresh_token")
}

class TokenDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TokenDao>(TokenTable)

    val userId by TokenTable.userId
    val accessToken by TokenTable.accessToken
    val refreshToken by TokenTable.refreshToken
}
