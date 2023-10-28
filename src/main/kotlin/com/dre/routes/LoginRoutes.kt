package com.dre.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.dre.model.User
import com.dre.model.UserSession
import com.dre.model.userStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.util.*

fun Route.loginRouting() {
//    authenticate("myauth1", strategy = AuthenticationStrategy.Required) {
//        get("/login") {
//            call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}")
//        }
//    }

    authenticate("myauth2", strategy = AuthenticationStrategy.Required) {
        post ("/login") {
            val username = call.principal<UserIdPrincipal>()?.name.toString()
            call.sessions.set(UserSession(name = username, count = 0))
            call.respondText("Hello, $username")
        }
    }

    val secret = environment?.config?.property("jwt.secret")?.getString()
    val issuer = environment?.config?.property("jwt.issuer")?.getString()
    val audience = environment?.config?.property("jwt.audience")?.getString()

    post("/jwt_login") {
        val userReceive = call.receive<User>()

        val email = userStorage.find { it.email == userReceive.email }
        val pass = userStorage.find { it.pass == userReceive.pass.hashString() }

        when {
            email == null -> {
                val error = hashMapOf(
                    "status_code" to HttpStatusCode.Unauthorized.value.toString(),
                    "message" to "Email not found"
                )
                call.respond(HttpStatusCode.Unauthorized, error)
                return@post
            }
            pass == null -> {
                val error = hashMapOf(
                    "status_code" to HttpStatusCode.Unauthorized.value.toString(),
                    "message" to "Password not found"
                )
                call.respond(HttpStatusCode.Unauthorized, error)
                return@post
            }
        }

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", userReceive.email)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(HMAC256(secret))

        // one of the way to create json object
        call.respond(hashMapOf("token" to token))
    }
}