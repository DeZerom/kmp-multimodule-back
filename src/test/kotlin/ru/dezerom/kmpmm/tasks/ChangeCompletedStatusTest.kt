package ru.dezerom.kmpmm.tasks

import io.ktor.client.call.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertFalse
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.requests.makePatch
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.auth.routing.dto.CredentialsDto
import ru.dezerom.kmpmm.features.auth.routing.dto.TokensDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTaskDto
import ru.dezerom.kmpmm.tools.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChangeCompletedStatusTest {
    private val creds1 = CredentialsDto("change_completed_status_test_1", "bfakjsnd1")
    private lateinit var tokens1: TokensDto

    private val creds2 = CredentialsDto("change_completed_status_test_2", "bfakjsnd1")
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

            task1 = createAndGetTask(CreateTaskDto(name = "task1"), tokens1.accessToken)
            task2 = createAndGetTask(CreateTaskDto(name = "task2"), tokens2.accessToken)
        }
    }

    @Test
    fun wrongData() = testApplication {
        createApp()
        createCustomClient().apply {
            assertWrongDataFormat(
                makePatch(
                    "${Urls.Tasks.CHANGE_COMPLETE_STATUS}/123",
                    authHeader = tokens1.accessToken
                )
            )
        }
    }

    @Test
    fun notFound() = testApplication {
        createApp()
        createCustomClient().apply {
            assertNotFound(
                makePatch(
                    "${Urls.Tasks.CHANGE_COMPLETE_STATUS}/81d1461a-e48a-4873-b83d-7a7ccafbcf26",
                    authHeader = tokens1.accessToken
                )
            )
        }
    }

    @Test
    fun authError() = testApplication {
        createApp()
        createCustomClient().apply {
            assertAccessDenied(
                makePatch(
                    "${Urls.Tasks.CHANGE_COMPLETE_STATUS}/${task1.id}",
                    authHeader = tokens2.accessToken
                )
            )
        }
    }

    @Test
    fun success() = testApplication {
        createApp()
        createCustomClient().apply {
            assertOkBoolResponse(
                makePatch(
                    "${Urls.Tasks.CHANGE_COMPLETE_STATUS}/${task1.id}",
                    authHeader = tokens1.accessToken
                )
            )

            val completedTask = getTask(tokens1.accessToken)
            val now = System.currentTimeMillis()
            assertTrue(completedTask.isCompleted)
            assertTrue(completedTask.completedAt in (now - 1000)..(now + 1000))

            assertOkBoolResponse(
                makePatch(
                    "${Urls.Tasks.CHANGE_COMPLETE_STATUS}/${task1.id}",
                    authHeader = tokens1.accessToken
                )
            )

            val notCompletedTask = getTask(tokens1.accessToken)
            assertFalse(notCompletedTask.isCompleted)
            assertNull(notCompletedTask.completedAt)
        }
    }

}
