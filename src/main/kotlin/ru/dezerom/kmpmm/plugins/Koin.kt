package ru.dezerom.kmpmm.plugins

import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.dezerom.kmpmm.Config
import ru.dezerom.kmpmm.features.auth.data.repository.AuthRepository
import ru.dezerom.kmpmm.features.auth.data.sources.TokenSource
import ru.dezerom.kmpmm.features.auth.data.sources.UserSource
import ru.dezerom.kmpmm.features.auth.domain.services.AuthService

fun Application.configureKoin(
    appConfig: ApplicationConfig = environment.config
) {
    install(Koin) {
        slf4jLogger()
        modules(
            configModule(appConfig),
            authModule
        )
    }
}

private fun configModule(config: ApplicationConfig) = module {
    single { Config(config) }
}

private val authModule = module {
    single { UserSource() }
    single { TokenSource() }
    single { AuthRepository(userSource = get(), tokenSource = get()) }
    single { AuthService(authRepository = get(), config = get()) }
}
