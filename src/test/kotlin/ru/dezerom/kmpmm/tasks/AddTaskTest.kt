package ru.dezerom.kmpmm.tasks

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddTaskTest {
    private val creds = CredentialsDto("xfnaksldm", "uiabdcjk")
    private lateinit var tokens: TokensDto

    @BeforeTest
    fun prepareData() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.Auth.REGISTER, creds)
            tokens = makePost(Urls.Auth.AUTHORIZE, creds).body<Response<TokensDto>>().body
        }
    }

    @Test
    fun addTask() = testApplication {
        createApp()
        createCustomClient().apply {
            assertOk(makePost(Urls.Tasks.CREATE, authHeader = tokens.accessToken, body = CreateTaskDto("qwe")))
            assertOk(makePost(
                Urls.Tasks.CREATE,
                authHeader = tokens.accessToken,
                body = CreateTaskDto("asd", "asd")
            ))
            assertOk(makePost(
                Urls.Tasks.CREATE,
                authHeader = tokens.accessToken,
                body = CreateTaskDto("zxc", "sadq", System.currentTimeMillis())
            ))
        }
    }

    @Test
    fun noData() = testApplication {
        createApp()
        createCustomClient().apply {
            val resp = makePost(Urls.Tasks.CREATE, authHeader = tokens.accessToken)
            assertEquals(HttpStatusCode.BadRequest, resp.status)

            val body = resp.body<Response<String>>()
            assertFalse(body.success)
            assertEquals(StringConst.Errors.NO_DATA, body.body)
        }
    }

    @Test
    fun wrongData() = testApplication {
        createApp()
        createCustomClient().apply {
            val resp = makePost(Urls.Tasks.CREATE, authHeader = tokens.accessToken, body = CreateTaskDto())
            assertEquals(HttpStatusCode.BadRequest, resp.status)

            val body = resp.body<Response<String>>()
            assertFalse(body.success)
            assertEquals(StringConst.Errors.NO_DATA, body.body)
        }
    }

    private suspend fun assertOk(resp: HttpResponse) {
        assertEquals(HttpStatusCode.OK, resp.status)

        val body = resp.body<Response<BoolResponse>>()
        assertTrue(body.success)
        assertTrue(body.body.response)
    }

}