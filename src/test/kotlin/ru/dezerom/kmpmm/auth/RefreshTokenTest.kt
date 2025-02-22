package ru.dezerom.kmpmm.auth

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makeGet
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.auth.routing.dto.UserDto
import ru.dezerom.kmpmm.tools.TEST_ACCESS_TOKEN_TIMEOUT
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class RefreshTokenTest {
    //refresh
    private val creds1 = CredentialsDto("cnvjsbdk", "19gwfbiqlks")
    private val creds2 = CredentialsDto("d1wfvqi", "nd1u9wgidbaj")

    private lateinit var tokens1: TokensDto
    private lateinit var tokens2: TokensDto

    //wrong
    private val creds3 = CredentialsDto("cnqlwdnska", "fnu1ndsa")

    private lateinit var tokens3: TokensDto

    //double use
    private val creds6 = CredentialsDto("dajbklcnq", "198uscb")

    private lateinit var tokens6: TokensDto

    //works after error
    private val creds7 = CredentialsDto("19sona", "1uhjasd1")

    private lateinit var tokens7: TokensDto

    @BeforeTest
    fun prepareData() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.Auth.REGISTER, creds1)
            makePost(Urls.Auth.REGISTER, creds2)
            makePost(Urls.Auth.REGISTER, creds3)
            makePost(Urls.Auth.REGISTER, creds6)
            makePost(Urls.Auth.REGISTER, creds7)

            tokens1 = makePost(Urls.Auth.AUTHORIZE, creds1).body<Response<TokensDto>>().body
            tokens2 = makePost(Urls.Auth.AUTHORIZE, creds2).body<Response<TokensDto>>().body
            tokens3 = makePost(Urls.Auth.AUTHORIZE, creds3).body<Response<TokensDto>>().body
            tokens6 = makePost(Urls.Auth.AUTHORIZE, creds6).body<Response<TokensDto>>().body
            tokens7 = makePost(Urls.Auth.AUTHORIZE, creds7).body<Response<TokensDto>>().body

            delay(TEST_ACCESS_TOKEN_TIMEOUT + 500)
            assertWrongAuth(makeGet(Urls.Auth.ME, authHeader = tokens1.accessToken))
            assertWrongAuth(makeGet(Urls.Auth.ME, authHeader = tokens2.accessToken))
            assertWrongAuth(makeGet(Urls.Auth.ME, authHeader = tokens3.accessToken))
            assertWrongAuth(makeGet(Urls.Auth.ME, authHeader = tokens6.accessToken))
            assertWrongAuth(makeGet(Urls.Auth.ME, authHeader = tokens7.accessToken))
        }
    }

    @Test
    fun testRefresh() = testApplication {
        createApp()
        createCustomClient().apply {
            tokens1 = assertOk(makePost(Urls.Auth.REFRESH, authHeader = tokens1.refreshToken))
            tokens2 = assertOk(makePost(Urls.Auth.REFRESH, authHeader = tokens2.refreshToken))

            assertNotEquals(tokens1.accessToken, tokens2.accessToken)
            assertNotEquals(tokens1.refreshToken, tokens2.refreshToken)

            val user1 = assertWorks(makeGet(Urls.Auth.ME, authHeader = tokens1.accessToken))
            val user2 = assertWorks(makeGet(Urls.Auth.ME, authHeader = tokens2.accessToken))

            assertEquals(user1.login, creds1.login)
            assertEquals(user2.login, creds2.login)
        }
    }

    @Test
    fun testWrongRefreshToken() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongAuth(makePost(Urls.Auth.REFRESH))
            assertWrongAuth(makePost(Urls.Auth.REFRESH, authHeader = "qnsjoqdsalnd"))
            assertWrongAuth(makePost(Urls.Auth.REFRESH, authHeader = tokens3.accessToken))
        }
    }

    @Test
    fun testDoubleUseRefreshToken() = testApplication {
        createApp()
        createCustomClient().apply {
            assertOk(makePost(Urls.Auth.REFRESH, authHeader = tokens6.refreshToken))
            assertWrongAuth(makePost(Urls.Auth.REFRESH, authHeader = tokens6.refreshToken))
        }
    }

    @Test
    fun testWorksAfterError() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongAuth(makePost(Urls.Auth.REFRESH, authHeader = "tokens7.refreshToken"))
            tokens7 = assertOk(makePost(Urls.Auth.REFRESH, authHeader = tokens7.refreshToken))

            assertWorks(makeGet(Urls.Auth.ME, authHeader = tokens7.accessToken))
        }
    }

    private suspend fun assertWorks(response: HttpResponse): UserDto {
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<Response<UserDto>>()
        assertTrue(body.success)
        assertTrue(body.body.id.isNotBlank())
        assertTrue(body.body.login.isNotBlank())

        return body.body
    }

    private suspend fun assertOk(response: HttpResponse): TokensDto {
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<Response<TokensDto>>()
        assertTrue(body.success)
        assertTrue(body.body.accessToken.isNotBlank())
        assertTrue(body.body.refreshToken.isNotBlank())

        return body.body
    }

    private suspend fun assertWrongAuth(response: HttpResponse) {
        val err = response.body<Response<String?>>()

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(StringConst.Errors.AUTH_ERROR, err.error)
        assertFalse(err.success)
    }
}
