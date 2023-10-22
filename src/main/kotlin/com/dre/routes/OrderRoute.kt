package com.dre.routes

import com.dre.model.UserSession
import com.dre.model.orderStorage
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.listOrderRoute() {
    authenticate("auth-session", strategy = AuthenticationStrategy.Required) {
        get("/order") {
            val userSession = call.principal<UserSession>()
            call.sessions.set(userSession?.copy(count = userSession.count + 1))
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage)
            }
        }
    }
}

fun Route.getOrderRoute() {
    get("/order/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = io.ktor.http.HttpStatusCode.NotFound
        )

        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "No order with id $id",
            status = io.ktor.http.HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id?}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = io.ktor.http.HttpStatusCode.NotFound
        )

        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "No order with id $id",
            status = io.ktor.http.HttpStatusCode.NotFound
        )
        val total = order.contents.sumOf { it.price * it.amount }
        call.respond(total)
    }
}