package com.dre.plugins

import com.dre.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        jwtTestRoute()
        customerRouting()
        listOrderRoute()
        getOrderRoute()
        totalizeOrderRoute()
        signUpRoute()
        loginRouting()
        logoutRouting()
    }
}
