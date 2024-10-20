package ru.dezerom.kmpmm.auth

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.tools.Urls
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationTest {

    @Test
    fun testRegistration() = testApplication {
        createApp()

        createCustomClient().apply {
            assertOk(makePost(url = Urls.REG, body = CredentialsDto(login = "qwe", password = "qwe")))
        }
    }

    @Test
    fun testWrongCredentials() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongCredentials(makePost(url = Urls.REG))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = null, password = null)))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = "", password = null)))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = null, password = "")))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = "", password = "")))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = "qwe", password = null)))
            assertWrongCredentials(makePost(url = Urls.REG, body = CredentialsDto(login = null, password = "qwe")))
        }
    }

    @Test
    fun testMultipleRegistration() = testApplication {
        createApp()
        createCustomClient().apply {
            assertOk(makePost(url = Urls.REG, body = CredentialsDto(login = "asd", password = "asd")))
            assertAlreadyExists(makePost(url = Urls.REG, body = CredentialsDto(login = "asd", password = "qwe")))
            assertAlreadyExists(makePost(url = Urls.REG, body = CredentialsDto(login = "asd", password = "asd")))
        }
    }

    private suspend fun assertOk(resp: HttpResponse) {
        assertEquals(HttpStatusCode.OK, resp.status)

        val respBody = resp.body<Response<BoolResponse>>()
        assertEquals(true, respBody.success)
        assertEquals(true, respBody.body.response)
    }

    private suspend fun assertWrongCredentials(resp: HttpResponse) {
        assertEquals(HttpStatusCode.BadRequest, resp.status)

        val respBody = resp.body<Response<String>>()
        assertEquals(false, respBody.success)
        assertEquals(StringConst.Errors.NO_CREDENTIALS, respBody.body)
    }

    private suspend fun assertAlreadyExists(resp: HttpResponse) {
        assertEquals(HttpStatusCode.BadRequest, resp.status)

        val respBody = resp.body<Response<String>>()
        assertEquals(false, respBody.success)
        assertEquals(StringConst.Errors.USER_ALREADY_EXISTS, respBody.body)
    }
}
