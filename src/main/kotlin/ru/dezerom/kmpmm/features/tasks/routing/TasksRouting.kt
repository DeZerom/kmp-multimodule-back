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

private const val TASK_ID_PARAM = "id"

fun Application.configureTasksRouting() {
    val tasksService: TasksService by inject()

    routing {
        authenticate(JWT_AUTH_TOKEN) {
            post(Urls.Tasks.CREATE) {
                call.makeResponse { tasksService.createTask(call.principal(), call.receiveNullable()) }
            }

            post(Urls.Tasks.EDIT) {
                call.makeResponse { tasksService.editTask(call.principal(), call.receiveNullable()) }
            }

            patch("${Urls.Tasks.CHANGE_COMPLETE_STATUS}/{$TASK_ID_PARAM}") {
                call.makeResponse {
                    tasksService.changeCompleteStatus(call.principal(), call.parameters[TASK_ID_PARAM])
                }
            }

            delete("${Urls.Tasks.DELETE}/{$TASK_ID_PARAM}") {
                call.makeResponse { tasksService.deleteTask(call.principal(), call.parameters[TASK_ID_PARAM]) }
            }

            get(Urls.Tasks.GET_ALL) {
                call.makeResponse { tasksService.getTasks(call.principal()) }
            }
        }
    }
}
