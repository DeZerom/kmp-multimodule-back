package ru.dezerom.kmpmm.common.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun createClient() = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}
