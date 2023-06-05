package com.github.stephenWanjala

import com.github.stephenWanjala.kTodo.TodoTable
import com.github.stephenWanjala.kTodo.TokenConfig
import com.github.stephenWanjala.kTodo.TrainingTaskTable
import com.github.stephenWanjala.kTodo.UserTable
import com.github.stephenWanjala.plugins.configureMonitoring
import com.github.stephenWanjala.plugins.configureRouting
import com.github.stephenWanjala.plugins.configureSecurity
import com.github.stephenWanjala.plugins.configureSerialization
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/todo_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = ""
    )
    transaction {
        SchemaUtils.create(UserTable, TodoTable, TrainingTaskTable)
    }
    configureSerialization()
    configureSecurity(tokenConfig)
    configureMonitoring()
    configureRouting(config = tokenConfig)

}
