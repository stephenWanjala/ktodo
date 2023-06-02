package com.github.stephenWanjala.kTodo

import com.github.stephenWanjala.kTodo.model.Todo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.todos() {
    get("/todos") {
        val todos = transaction {
            TodoTable.selectAll().map { resultRow ->
                Todo(
                    id = resultRow[TodoTable.id].value,
                    title = resultRow[TodoTable.title],
                    completed = resultRow[TodoTable.completed]
                )
            }
        }
        call.respond(status = HttpStatusCode.OK, todos)
    }
}


fun Route.createTodo(){
    post("/todo") {
        val todo = call.receive<Todo>()
        val insertedTodo = transaction {
            TodoTable.insertAndGetId {
                it[title] = todo.title
                it[completed] = todo.completed
            }
        }
        call.respond(HttpStatusCode.Created, mapOf("id" to insertedTodo.value))
    }
}

fun Route.getTodo(){
    get ("/todo/{id}"){
        val todoId = call.parameters["id"]?.toIntOrNull()
        if (todoId != null) {
            val todo = transaction<Todo?> {
                TodoTable.select { TodoTable.id eq todoId }.singleOrNull()?.let {
                    Todo(
                        id = it[TodoTable.id].value,
                        title = it[TodoTable.title],
                        completed = it[TodoTable.completed]
                    )
                }
            }
            if (todo != null) {
                call.respond(todo)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
        }
    }
}


fun Route.updateTodo(){
    patch("/todo/{id}") {
        val todoId = call.parameters["id"]?.toIntOrNull()
        if (todoId != null) {
            val updatedTodo = call.receive<Todo>()
            val rowsUpdated = transaction {
                TodoTable.update({ TodoTable.id eq todoId }) {
                    it[title] = updatedTodo.title
                    it[completed] = updatedTodo.completed
                }
            }
            if (rowsUpdated > 0) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
        }
    }
}


fun Route.deleteTodo(){
    delete("/todo/{id}") {
        val todoId = call.parameters["id"]?.toIntOrNull()
        if (todoId != null) {
            val rowsDeleted = transaction {
                TodoTable.deleteWhere { TodoTable.id eq todoId }
            }
            if (rowsDeleted > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid todo ID")
        }
    }
}