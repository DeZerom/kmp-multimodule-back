package ru.dezerom.kmpmm.tasks

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.requests.makeGet
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.edit.EditTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.*

class EditTaskTest {
    private val creds1 = CredentialsDto("edit_tast_test_1_1", "123edasf")
    private lateinit var tokens1: TokensDto

    private val creds2 = CredentialsDto("edit_tast_test_1_2", "123edasf")
    private lateinit var tokens2: TokensDto

    private lateinit var task1: GetTaskDto
    private lateinit var task2: GetTaskDto

    @BeforeTest
    fun prepareData() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.Auth.REGISTER, creds1)
            tokens1 = makePost(Urls.Auth.AUTHORIZE, creds1).body<Response<TokensDto>>().body

            makePost(Urls.Auth.REGISTER, creds2)
            tokens2 = makePost(Urls.Auth.AUTHORIZE, creds2).body<Response<TokensDto>>().body

            task1 = createAndGet(CreateTaskDto(name = "qwe"), tokens1.accessToken)
            task2 = createAndGet(CreateTaskDto(name = "asd"), tokens2.accessToken)
        }
    }

    @Test
    fun noData() = testApplication {
        createApp()
        createCustomClient().apply {
            assertsNoData(
                makePost<EditTaskDto?>(
                    Urls.Tasks.EDIT,
                    null,
                    tokens1.accessToken
                )
            )

            assertsNoData(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(),
                    tokens1.accessToken
                )
            )

            assertsNoData(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(id = ""),
                    tokens1.accessToken
                )
            )

            assertsNoData(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(name = ""),
                    tokens1.accessToken
                )
            )

            assertsNoData(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(id = "", name = ""),
                    tokens1.accessToken
                )
            )
        }
    }

    @Test
    fun wrongId() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongDataFormat(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(id = "123", name = "qwe"),
                    tokens1.accessToken
                )
            )
        }
    }

    @Test
    fun notFound() = testApplication {
        createApp()
        createCustomClient().apply {
            assertNotFound(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(id = "81d1461a-e48a-4873-b83d-7a7ccafbcf26", name = "1weq"),
                    tokens1.accessToken
                )
            )
        }
    }

    @Test
    fun noAccess() = testApplication {
        createApp()
        createCustomClient().apply {
            assertAccessDenied(
                makePost(
                    Urls.Tasks.EDIT,
                    EditTaskDto(id = task1.id, name = "123"),
                    tokens2.accessToken
                )
            )
        }
    }

    @Test
    fun edit() = testApplication {
        createApp()
        createCustomClient().apply {
            val editTask = EditTaskDto(
                id = task2.id,
                name = "123",
                description = "456",
                System.currentTimeMillis() + 60000
            )

            val resp = makePost(
                Urls.Tasks.EDIT,
                editTask,
                tokens2.accessToken
            )

            assertEquals(HttpStatusCode.OK, resp.status)

            val body = resp.body<Response<BoolResponse>>()
            assertTrue(body.success)
            assertTrue(body.body.response)

            val editedTask = getTask(tokens2.accessToken)
            assertEquals(editTask.id, editedTask.id)
            assertEquals(editTask.name, editedTask.name)
            assertEquals(editTask.description, editedTask.description)
            assertEquals(editTask.deadline, editedTask.deadline)
        }
    }

    private suspend fun assertsNoData(response: HttpResponse) =
        assertError(response, HttpStatusCode.BadRequest, StringConst.Errors.NO_DATA)

    private suspend fun assertWrongDataFormat(response: HttpResponse) =
        assertError(response, HttpStatusCode.BadRequest, StringConst.Errors.WRONG_DATA_FORMAT)

    private suspend fun assertNotFound(response: HttpResponse) =
        assertError(response, HttpStatusCode.NotFound, StringConst.Errors.NO_SUCH_DATA)

    private suspend fun assertAccessDenied(response: HttpResponse) =
        assertError(response, HttpStatusCode.Forbidden, StringConst.Errors.AUTH_ERROR)

    private suspend fun assertError(response: HttpResponse, status: HttpStatusCode, message: String) {
        assertEquals(status, response.status)

        val body = response.body<Response<String>>()
        assertFalse(body.success)
        assertEquals(message, body.body)
    }

    private suspend fun HttpClient.createAndGet(task: CreateTaskDto, token: String): GetTaskDto {
        makePost(Urls.Tasks.CREATE, task, token)

        return getTask(token)
    }

    private suspend fun HttpClient.getTask(token: String): GetTaskDto {
        return makeGet(Urls.Tasks.GET_ALL, token).body<Response<GetTasksDto>>().body.tasks.first()
    }
}
