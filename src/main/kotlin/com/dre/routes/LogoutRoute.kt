package com.dre.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.logoutRouting() {
    authenticate("auth-session", strategy = AuthenticationStrategy.Required) {
        get ("/logout") {
            call.sessions.clear("auth-session")
            call.respondText("Logged out")
        }
    }
}