package com.github.stephenWanjala.kTodo

import com.github.stephenWanjala.kTodo.model.Todo
import com.github.stephenWanjala.kTodo.model.TodoRequest
import com.github.stephenWanjala.kTodo.model.UserRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.todos() {
    authenticate {
        get("/todos") {
            val user = call.principal<UserIdPrincipal>()
            val userId = user?.let { getUserIdByUsername(it.name) }
            if (userId != null) {
                val todos = transaction {
                    TodoTable.select { TodoTable.userId eq userId }.map {
                        Todo(
                            id = it[TodoTable.id].value,
                            title = it[TodoTable.title],
                            completed = it[TodoTable.completed],
                            userId = it[TodoTable.userId].value
                        )
                    }
                }
                call.respond(status = HttpStatusCode.OK, todos)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
            }
        }
    }
}


fun Route.createTodo() {
    post("/todo") {
        val user = call.principal<UserIdPrincipal>()
        val userId = user?.let { getUserIdByUsername(it.name) }
        if (userId != null) {
            val todoRequest = call.receive<TodoRequest>()
            val insertedTodo = transaction {
                if (todoRequest.isTrainingTask) {
                    TrainingTaskTable.insertAndGetId {
                        it[title] = todoRequest.title
                        it[completed] = todoRequest.completed
                        it[this.userId] = EntityID(userId, UserTable)
                    }
                } else {
                    TodoTable.insertAndGetId {
                        it[title] = todoRequest.title
                        it[completed] = todoRequest.completed
                        it[this.userId] = EntityID(userId, UserTable)
                    }
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to insertedTodo.value))
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user")
        }
    }
    }


fun Route.getTodo() {
    get("/todo/{id}") {
        val todoId = call.parameters["id"]?.toIntOrNull()
        if (todoId != null) {
            val todo = transaction<Todo?> {
                TodoTable.select { TodoTable.id eq todoId }.singleOrNull()?.let {
                    Todo(
                        id = it[TodoTable.id].value,
                        title = it[TodoTable.title],
                        completed = it[TodoTable.completed],
                        userId = it[TodoTable.userId].value
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


fun Route.updateTodo() {
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


fun Route.deleteTodo() {
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

fun Route.registerUser(){
    post ("/register"){
        val userRequest = call.receive<UserRequest>()
        val existingUser = transaction {
            UserTable.select { UserTable.username eq userRequest.username }.singleOrNull()
        }
        if (existingUser == null) {
            val hashedPassword = hashPassword(userRequest.password)
            val userId = transaction {
                UserTable.insertAndGetId {
                    it[username] = userRequest.username
                    it[password] = hashedPassword
                }
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to userId.value))
        } else {
            call.respond(HttpStatusCode.BadRequest, "Username already exists")
        }
    }
}

fun Route.loginUser(){
    post("/login") {
        val userRequest = call.receive<UserRequest>()
        val user = transaction {
            UserTable.select { UserTable.username eq userRequest.username }.singleOrNull()
        }
        if (user != null && verifyPassword(userRequest.password, user[UserTable.password])) {
            val token = generateToken(user[UserTable.username])
            call.respond(mapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}

fun generateToken(username: String): String {
    TODO("Not yet implemented")
}

fun getUserIdByUsername(username: String): Int? {
    return transaction {
        UserTable.select { UserTable.username eq username }
            .singleOrNull()
            ?.get(UserTable.id)
            ?.value
    }
}


fun generateSalt(): String {
    return BCrypt.gensalt()
}

fun hashPassword(password: String): String {
    val salt = generateSalt()
    return BCrypt.hashpw(password, salt)
}

fun verifyPassword(password: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(password, hashedPassword)
}

