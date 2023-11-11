package com.dre.routes

import com.dre.database.dao
import com.dre.model.User
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.security.MessageDigest

fun Route.signUpRoute() {
    post ("/signup") {
        val user = call.receive<User>()
        val existing_user = dao.getUserFromEmail(user.email)
        if (existing_user != null) {
            return@post call.respondText("User already exists", status = io.ktor.http.HttpStatusCode.BadRequest)
        }
        val hashedUser = user.copy(pass = user.pass.hashString())
        val result = dao.addUser(email = hashedUser.email, hashedUser.pass)
        result?.let {
            call.respondText("User stored correctly", status = io.ktor.http.HttpStatusCode.OK)
        } ?: call.respondText("Something wrong with our system when save your information, please try again", status = io.ktor.http.HttpStatusCode.BadRequest)
    }
}

fun String.hashString(): String {
    val bytes = toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}