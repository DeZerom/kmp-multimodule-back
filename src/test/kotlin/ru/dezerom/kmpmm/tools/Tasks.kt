package ru.dezerom.kmpmm.tools

import io.ktor.client.*
import io.ktor.client.call.*
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.requests.makeGet
import ru.dezerom.kmpmm.common.requests.makePost
import ru.dezerom.kmpmm.common.responds.Response
import ru.dezerom.kmpmm.features.tasks.routing.dto.create.CreateTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTaskDto
import ru.dezerom.kmpmm.features.tasks.routing.dto.get.GetTasksDto

suspend fun HttpClient.createAndGetTask(task: CreateTaskDto, token: String): GetTaskDto {
    makePost(Urls.Tasks.CREATE, task, token)

    return getTask(token)
}

suspend fun HttpClient.getTask(token: String): GetTaskDto {
    return makeGet(Urls.Tasks.GET_ALL, token).body<Response<GetTasksDto>>().body.tasks.first()
}
