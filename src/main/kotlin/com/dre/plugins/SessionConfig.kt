package com.dre.plugins

import com.dre.model.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<UserSession>("auth-session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
        }
    }
}