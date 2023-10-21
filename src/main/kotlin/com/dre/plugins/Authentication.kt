package com.dre.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    authentication {
        basic(name = "myauth1") {
            realm = "Ktor Server"
            validate { credentials ->
                if (credentials.name == credentials.password) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        form(name = "myauth2") {
            userParamName = "username"
            passwordParamName = "password"

            validate {
                if (it.name == "oink" && it.password == "oink_123") {
                    UserIdPrincipal(it.name)
                } else {
                    null
                }
            }

            // action when authentication failed
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
    }
}