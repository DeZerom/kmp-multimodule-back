package ru.dezerom.kmpmm.features.tasks.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.dezerom.kmpmm.Urls
import ru.dezerom.kmpmm.common.utils.makeResponse
import ru.dezerom.kmpmm.features.tasks.domain.services.TasksService
import ru.dezerom.kmpmm.plugins.JWT_AUTH_TOKEN

fun Application.configureTasksRouting() {
    val tasksService: TasksService by inject()

    routing {
        authenticate(JWT_AUTH_TOKEN) {
            post(Urls.Tasks.CREATE) {
                call.makeResponse { tasksService.createTask(call.principal(), call.receiveNullable()) }
            }

            get(Urls.Tasks.GET_ALL) {
                call.makeResponse { tasksService.getTasks(call.principal()) }
            }
        }
    }
}
