package ru.dezerom.kmpmm.common.requests

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun HttpClient.makePost(url: String) = post {
    url(url)
    headers {
        accept(ContentType.Application.Json)
        contentType(ContentType.Application.Json)
    }
}

suspend inline fun <reified T> HttpClient.makePost(
    url: String,
    body: T,
) = post {
    url(url)
    headers {
        accept(ContentType.Application.Json)
        contentType(ContentType.Application.Json)
    }
    setBody(body)
}
