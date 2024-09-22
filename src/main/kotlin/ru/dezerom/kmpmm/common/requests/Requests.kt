package ru.dezerom.kmpmm.common.requests

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun HttpClient.makePost(
    url: String
) = post {
    url(url)
    headers {
        accept(ContentType.Application.Json)
    }
}
