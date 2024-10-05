package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.data.sources.TokenSource
import ru.dezerom.kmpmm.features.auth.data.sources.UserSource
import ru.dezerom.kmpmm.features.auth.domain.services.AuthService

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(authModule)
    }
}

private val authModule = module {
    single { UserSource() }
    single { TokenSource() }
    single { AuthRepository(userSource = get(), tokenSource = get()) }
    single { AuthService(get()) }
}
