package com.dre.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.loginRouting() {
    authenticate("myauth1", strategy = AuthenticationStrategy.Required) {
        get("/login") {
            call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}")
        }
    }

    authenticate("myauth2", strategy = AuthenticationStrategy.Required) {
        post ("/login") {
            call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}")
        }
    }
}