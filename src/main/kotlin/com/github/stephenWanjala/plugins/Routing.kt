package com.github.stephenWanjala.plugins

import com.github.stephenWanjala.kTodo.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(config: TokenConfig) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        todos()
        createTodo()
        getTodo()
        updateTodo()
        deleteTodo()
        registerUser()
        loginUser(config = config)

    }
}
