package ru.dezerom.kmpmm.common.requests

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun HttpClient.makePost(
    url: String,
    authHeader: String? = null
) = post {
    basePostSettings(url, authHeader)
}

suspend inline fun <reified T> HttpClient.makePost(
    url: String,
    body: T,
    authHeader: String? = null,
) = post {
    this.basePostSettings(url, authHeader)
    setBody(body)
}

suspend fun HttpClient.makeGet(
    url: String,
    authHeader: String? = null
) = get {
    url(url)
    headers {
        authHeader?.let { bearerAuth(authHeader) }
    }
}

suspend fun HttpClient.makePatch(
    url: String,
    authHeader: String? = null,
) = patch {
    url(url)
    setDefaultHeaders(authHeader)
}

suspend inline fun <reified T> HttpClient.makePatch(
    url: String,
    body: T,
    authHeader: String? = null,
) = patch {
    url(url)
    setDefaultHeaders(authHeader)

    if (body != null) setBody(body)
}

fun HttpRequestBuilder.basePostSettings(url: String, authHeader: String?) {
    url(url)
    setDefaultHeaders(authHeader)
}

fun HttpRequestBuilder.setDefaultHeaders(authHeader: String?) {
    headers {
        accept(ContentType.Application.Json)
        contentType(ContentType.Application.Json)

        authHeader?.let { bearerAuth(authHeader) }
    }
}
