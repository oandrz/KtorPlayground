package com.dre.plugins

import com.dre.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        customerRouting()
        listOrderRoute()
        getOrderRoute()
        totalizeOrderRoute()
        loginRouting()
    }
}
