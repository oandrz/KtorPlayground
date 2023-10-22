package com.dre.routes

import com.dre.model.User
import com.dre.model.userStorage
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.MessageDigest

fun Route.signUpRoute() {
    post ("/signup") {
        val user = call.receive<User>()
        if (userStorage.any { it.email == user.email }) {
            return@post call.respondText("User already exists", status = io.ktor.http.HttpStatusCode.BadRequest)
        }
        val hashedUser = user.copy(pass = user.pass.hashString())
        userStorage.add(hashedUser)
        call.respondText("User stored correctly", status = io.ktor.http.HttpStatusCode.OK)
    }
}

fun String.hashString(): String {
    val bytes = toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}