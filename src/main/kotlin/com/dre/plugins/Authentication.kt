package com.dre.plugins

import com.dre.model.UserSession
import com.dre.model.userStorage
import com.dre.routes.hashString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    val jwtConfig = JwtConfig.makeJwtConfig(environment.config)
    authentication {
//        setBasicBasedAuthentication()
        setFormBasedAuthentication()
        setJWTBasedAuthentication(jwtConfig)
        setSessionConfig()
    }
}

fun AuthenticationConfig.setBasicBasedAuthentication() {
    // using basic authentication
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
}

fun AuthenticationConfig.setFormBasedAuthentication() {
    // using form authentication - POST Method
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
}

fun AuthenticationConfig.setJWTBasedAuthentication(jwtConfig: JwtConfig) {
    jwt("auth-jwt") {
        realm = jwtConfig.realm

        // to verify token format and signature
        verifier(
            com.auth0.jwt.JWT
                .require(com.auth0.jwt.algorithms.Algorithm.HMAC256(jwtConfig.secret))
                .withAudience(jwtConfig.audience)
                .withIssuer(jwtConfig.issuer)
                .build()
        )

        validate { cred ->
            if (!cred.payload.getClaim("email").asString().isNullOrEmpty()) {
                JWTPrincipal(cred.payload)
            } else {
                null
            }
        }

        challenge { defaultScheme, realm ->
            call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
        }
    }
}

fun AuthenticationConfig.setSessionConfig() {
    // handle session
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

class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
) {
    companion object {
        fun makeJwtConfig(config: ApplicationConfig): JwtConfig {
            val secret = config.property("jwt.secret").getString()
            val issuer = config.property("jwt.issuer").getString()
            val audience = config.property("jwt.audience").getString()
            val myRealm = config.property("jwt.realm").getString()
            return JwtConfig(secret, issuer, audience, myRealm)
        }
    }
}
