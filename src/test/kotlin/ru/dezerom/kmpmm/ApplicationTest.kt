package ru.dezerom.kmpmm

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.StringResponse
import ru.dezerom.kmpmm.plugins.configureRouting
import ru.dezerom.kmpmm.plugins.configureSerialization
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testAuth() = testApplication {
        createApp()

        createCustomClient().apply {
            makePost("/register").apply {
                assertEquals(HttpStatusCode.OK, status)

                val resp = body<StringResponse>()
                assertEquals(resp.response, "Hello, World!")
            }
        }
    }

    private fun TestApplicationBuilder.createApp() {
        application {
            configureRouting()
            configureSerialization()
        }
    }

    private fun ApplicationTestBuilder.createCustomClient() = createClient {
        install(ContentNegotiation) {
            json()
        }
    }
}
