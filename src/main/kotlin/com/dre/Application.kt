package com.dre

import com.dre.database.DatabaseFactory
import com.dre.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSession()
    configureAuthentication()
    configureSerialization()
    configureRouting()
}
