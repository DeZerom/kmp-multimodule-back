package ru.dezerom.kmpmm.common.utils.security

import io.ktor.server.auth.*
import java.util.*

class UserIdPrinciple(val id: UUID): Principal
