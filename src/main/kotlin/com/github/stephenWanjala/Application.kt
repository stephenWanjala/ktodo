package com.github.stephenWanjala

import io.ktor.server.application.*
import com.github.stephenWanjala.plugins.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/todo_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = ""
    )
    configureSerialization()
    configureSecurity()
    configureMonitoring()
    configureRouting()

}
