package ru.dezerom.kmpmm.tools

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import junit.framework.TestCase.assertTrue
import ru.dezerom.kmpmm.common.constants.StringConst
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.common.responds.common.BoolResponse
import kotlin.test.assertEquals
import kotlin.test.assertFalse

suspend fun assertNoData(response: HttpResponse) =
    assertError(response, HttpStatusCode.BadRequest, StringConst.Errors.NO_DATA)

suspend fun assertWrongDataFormat(response: HttpResponse) =
    assertError(response, HttpStatusCode.BadRequest, StringConst.Errors.WRONG_DATA_FORMAT)

suspend fun assertNotFound(response: HttpResponse) =
    assertError(response, HttpStatusCode.NotFound, StringConst.Errors.NO_SUCH_DATA)

suspend fun assertAccessDenied(response: HttpResponse) =
    assertError(response, HttpStatusCode.Forbidden, StringConst.Errors.AUTH_ERROR)

suspend fun assertError(response: HttpResponse, status: HttpStatusCode, message: String) {
    assertEquals(status, response.status)

    val body = response.body<Response<String>>()
    assertFalse(body.success)
    assertEquals(message, body.body)
}

suspend fun assertOkBoolResponse(response: HttpResponse) {
    assertEquals(HttpStatusCode.OK, response.status)

    val body = response.body<Response<BoolResponse>>()
    assertTrue(body.success)
    assertTrue(body.body.response)
}
