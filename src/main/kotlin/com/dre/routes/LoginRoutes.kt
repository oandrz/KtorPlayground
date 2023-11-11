package com.dre.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC256
import com.dre.database.dao
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

    post("/jwt_login") { context ->
        val userReceive = call.receive<User>()

        val user = dao.checkUserExist(userReceive.email, userReceive.pass.hashString())

        when {
            user == null -> {
                val error = hashMapOf(
                    "status_code" to HttpStatusCode.Unauthorized.value.toString(),
                    "message" to "User not found"
                )
                call.respond(HttpStatusCode.Unauthorized, error)
                return@post
            }
        }

        val token = this@loginRouting.environment?.generateJwtToken(userReceive.email)

        // one of the way to create json object
        call.respond(hashMapOf("token" to token))
    }
}

private fun ApplicationEnvironment.generateJwtToken(email: String): String {
    val secret = config.property("jwt.secret").getString()
    val issuer = config.property("jwt.issuer").getString()
    val audience = config.property("jwt.audience").getString()

    return JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(HMAC256(secret))
}