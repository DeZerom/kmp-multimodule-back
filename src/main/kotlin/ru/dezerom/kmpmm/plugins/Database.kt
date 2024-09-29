package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable

fun Application.configureDB() {
    Database.connect(url = Secrets.DB_URL)

    transaction {
        SchemaUtils.create(UserTable)
    }
}
