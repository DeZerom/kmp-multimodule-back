package ru.dezerom.kmpmm.features.tasks.data.tables

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable
import java.util.*

object TaskTable: UUIDTable() {
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val title = text("name")
    val description = text("description").nullable()
    val deadline = long("deadline").nullable()
}

class TaskDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TaskDao>(TaskTable)

    var userId by TaskTable.userId
    var title by TaskTable.title
    var description by TaskTable.description
    var deadline by TaskTable.deadline
}