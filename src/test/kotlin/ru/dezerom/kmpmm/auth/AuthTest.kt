package ru.dezerom.kmpmm.auth

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthTest {
    private val firstCreds = CredentialsDto("6315786", "543456756")
    private val secondCreds = CredentialsDto("asdqcsa", "sdqnsklda")

    @BeforeTest
    fun registerUsers() = testApplication {
        createApp()

        createCustomClient().apply {
            makePost(Urls.Auth.REGISTER, firstCreds)
            makePost(Urls.Auth.REGISTER, secondCreds)
        }
    }

    @Test
    fun testAuth() = testApplication {
        createApp()

        createCustomClient().apply {
            makePost(Urls.Auth.AUTHORIZE, firstCreds).apply {
                assertEquals(HttpStatusCode.OK, status)
                assertAndReturnBody()
            }

            makePost(Urls.Auth.AUTHORIZE, secondCreds).apply {
                assertEquals(HttpStatusCode.OK, status)
                assertAndReturnBody()
            }
        }
    }

    @Test
    fun testNoCredentials() = testApplication {
        createApp()

        createCustomClient().apply {
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto(null, null)).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("qwe", null)).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto(null, "qwe")).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("", "")).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("qwe", "")).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("", "qwe")).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto(null, "")).assertNoCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("", null)).assertNoCredentials()
        }
    }

    @Test
    fun testWrongCredentials() = testApplication {
        createApp()

        createCustomClient().apply {
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto("qwe", "qwe")).assertWrongCredentials()
            makePost(Urls.Auth.AUTHORIZE, CredentialsDto(firstCreds.login, "qwe")).assertWrongCredentials()
        }
    }

    private suspend fun HttpResponse.assertAndReturnBody(): TokensDto {
        val body = body<Response<TokensDto>>()
        assertTrue { body.success }
        assertTrue { body.body.accessToken.isNotBlank() }
        assertTrue { body.body.refreshToken.isNotBlank() }

        return body.body
    }

    private suspend fun HttpResponse.assertNoCredentials() {
        assertEquals(HttpStatusCode.BadRequest, status)

        val body = body<Response<String>>()
        assertEquals(false, body.success)
        assertEquals(StringConst.Errors.NO_CREDENTIALS, body.body)
    }

    private suspend fun HttpResponse.assertWrongCredentials() {
        assertEquals(HttpStatusCode.Unauthorized, status)

        val body = body<Response<String>>()
        assertEquals(false, body.success)
        assertEquals(StringConst.Errors.WRONG_CREDENTIALS, body.body)
    }
}
