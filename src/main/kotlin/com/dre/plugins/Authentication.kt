package com.dre.plugins

import com.dre.model.UserSession
import com.dre.model.userStorage
import com.dre.routes.hashString
import io.ktor.http.*
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
            // replace form key
            userParamName = "email"
            passwordParamName = "password"

            validate { credentials ->
                val user = userStorage.find { it.email == credentials.name }
                val pass = userStorage.find { it.pass == credentials.password.hashString() }
                if (user != null && pass != null) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }

        session<UserSession>("auth-session") {
            validate { session ->
                if (userStorage.find { it.email == session.name } != null) {
                    session
                } else {
                    null
                }
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Session invalid")
            }
        }
    }
}