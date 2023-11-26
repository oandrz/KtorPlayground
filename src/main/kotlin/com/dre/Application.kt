package com.dre

import com.dre.database.DatabaseFactory
import com.dre.plugins.*
import io.ktor.server.application.*
/*
* TO build jar for beanstalk
* ./gradlew :buildFatJar then look at /build/libs
* */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    DatabaseFactory.init(environment.config)
    configureSession()
    configureAuthentication()
    configureSerialization()
    configureRouting()
}
