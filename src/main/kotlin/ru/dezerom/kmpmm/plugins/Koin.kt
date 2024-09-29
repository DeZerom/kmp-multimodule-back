package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.data.sources.UserSourceImpl
import ru.dezerom.kmpmm.features.auth.domain.services.AuthService

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(authModule)
    }
}

private val authModule = module {
    single { UserSourceImpl() }
    single { AuthRepository(get()) }
    single { AuthService(get()) }
}
