package com.dre.model

import io.ktor.server.auth.*

data class UserSession(val name: String, val count: Int) : Principal