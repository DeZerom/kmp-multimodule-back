package ru.dezerom.kmpmm.common.utils.security

import io.ktor.server.auth.*
import java.util.*

class UserIdAndTokenIdPrinciple(
    val userId: UUID,
    val tokenId: UUID,
): Principal
