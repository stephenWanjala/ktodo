package com.github.stephenWanjala

import com.github.stephenWanjala.kTodo.TodoTable
import com.github.stephenWanjala.kTodo.TrainingTaskTable
import com.github.stephenWanjala.kTodo.UserTable
import io.ktor.server.application.*
import com.github.stephenWanjala.plugins.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
    transaction {
        SchemaUtils.create(UserTable, TodoTable, TrainingTaskTable)
    }
    configureSerialization()
    configureSecurity()
    configureMonitoring()
    configureRouting()

}
