package ru.dezerom.kmpmm.tasks

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.requests.makeGet
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto
import ru.dezerom.kmpmm.tools.createApp
import ru.dezerom.kmpmm.tools.createCustomClient
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTasksTest {
    private val creds1 = CredentialsDto("get_tasks_test_1_1", "dbvkajcn")
    private lateinit var tokens1: TokensDto
    
    private val creds2 = CredentialsDto("get_tasks_test_1_2", "2345678")
    private lateinit var tokens2: TokensDto

    private val task1 = CreateTaskDto(name = "task1")
    private val task2 = CreateTaskDto(name = "task2", description = "bububu")
    private val task3 = CreateTaskDto(
        name = "task3",
        description = "pupupu",
        deadline = System.currentTimeMillis() + 60000
    )

    @BeforeTest
    fun prepareData() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.Auth.REGISTER, creds1)
            tokens1 = makePost(Urls.Auth.AUTHORIZE, creds1).body<Response<TokensDto>>().body

            makePost(Urls.Auth.REGISTER, creds2)
            tokens2 = makePost(Urls.Auth.AUTHORIZE, creds2).body<Response<TokensDto>>().body
        }
    }

    @Test
    fun getEmpty() = testApplication {
        createApp()
        createCustomClient().apply {
            val resp = assertOk(makeGet(Urls.Tasks.GET_ALL, tokens1.accessToken))
            assertTrue(resp.tasks.isEmpty())
        }
    }

    @Test
    fun addAndGet() = testApplication {
        createApp()
        createCustomClient().apply {
            makePost(Urls.Tasks.CREATE, task1, tokens2.accessToken)
            assertContainsTasks(makeGet(Urls.Tasks.GET_ALL, tokens2.accessToken), listOf(task1))

            makePost(Urls.Tasks.CREATE, task2, tokens2.accessToken)
            assertContainsTasks(makeGet(Urls.Tasks.GET_ALL, tokens2.accessToken), listOf(task1, task2))

            makePost(Urls.Tasks.CREATE, task3, tokens2.accessToken)
            assertContainsTasks(makeGet(Urls.Tasks.GET_ALL, tokens2.accessToken), listOf(task1, task2, task3))
        }
    }

    private suspend fun assertContainsTasks(response: HttpResponse, tasks: List<CreateTaskDto>) {
        val body = assertOk(response)

        assertEquals(tasks.size, body.tasks.size)

        tasks.forEach { task ->
            val found = body.tasks.find { it.name == task.name }

            assertEquals(task.name, found?.name)
            assertEquals(task.description, found?.description)
            assertEquals(task.deadline, found?.deadline)
        }
    }

    private suspend fun assertOk(response: HttpResponse): GetTasksDto {
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<Response<GetTasksDto>>()
        assertTrue(body.success)

        return body.body
    }
}
