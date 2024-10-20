package ru.dezerom.kmpmm.auth

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makeGet
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.auth.routing.dto.UserDto
import ru.dezerom.kmpmm.tools.Urls
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class GetMeTest {
    private val firstCreds = CredentialsDto("fnasdlwd", "1nbwdjqlna")
    private val secondCreds = CredentialsDto("vbahkbsdaj", "1wfdy18y1")

    private lateinit var firstTokens: TokensDto
    private lateinit var secondTokens: TokensDto

    @BeforeTest
    fun prepareData() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.REG, firstCreds)
            makePost(Urls.REG, secondCreds)

            firstTokens = makePost(Urls.AUTH, firstCreds).body<Response<TokensDto>>().body
            secondTokens = makePost(Urls.AUTH, secondCreds).body<Response<TokensDto>>().body
        }
    }

    @Test
    fun testGetMe() = testApplication {
        createApp()
        createCustomClient().apply {
            val f = assertOk(makeGet(Urls.ME, authHeader = firstTokens.accessToken))
            val s = assertOk(makeGet(Urls.ME, authHeader = secondTokens.accessToken))

            assertEquals(firstCreds.login,  f.login)
            assertEquals(secondCreds.login, s.login)

            assertNotEquals(f.id, s.id)
            assertNotEquals(f.login, s.login)
        }
    }

    @Test
    fun testNoAuth() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongAuth(makeGet(Urls.ME))
        }
    }

    @Test
    fun testWrongAuthHeader() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongAuth(makeGet(Urls.ME, authHeader = "asdq"))
        }
    }

    private suspend fun assertOk(response: HttpResponse): UserDto {
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<Response<UserDto>>()
        assertTrue(body.success)
        assertTrue(body.body.id.isNotBlank())
        assertTrue(body.body.login.isNotBlank())

        return body.body
    }

    private suspend fun assertWrongAuth(response: HttpResponse) {
        val err = response.body<Response<String>>()

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(StringConst.Errors.AUTH_ERROR, err.body)
        assertFalse(err.success)
    }
}
