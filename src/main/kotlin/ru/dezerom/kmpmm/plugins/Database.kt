package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.dezerom.kmpmm.Secrets
import ru.dezerom.kmpmm.features.auth.data.tables.TokenTable
import ru.dezerom.kmpmm.features.auth.data.tables.UserTable

fun Application.configureDB(test: Boolean = false) {
    if (test) {
        Database.connect("jdbc:h2:mem:test;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    } else {
        Database.connect(url = Secrets.DB_URL)
    }

    transaction {
        SchemaUtils.create(
            UserTable,
            TokenTable
        )
    }
}
