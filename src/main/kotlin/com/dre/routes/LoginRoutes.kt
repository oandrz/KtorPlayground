package com.dre.routes

import com.auth0.jwt.JWT
import com.dre.model.User
import com.dre.model.UserSession
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
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", userReceive.email)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secret))

        // one of the way to create json object
        call.respond(hashMapOf("token" to token))
    }
}