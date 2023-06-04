package com.github.stephenWanjala.kTodo

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val username = varchar("username", 100).uniqueIndex()
    val password = varchar("password", 100)
}

object TodoTable : IntIdTable() {
    val title = varchar("title", 100)
    val completed = bool("completed")
    val userId = reference("user_id", UserTable)
    val isTrainingTask = bool("is_training_task")
}

object TrainingTaskTable : IntIdTable() {
    val title = varchar("title", 100)
    val completed = bool("completed")
    val userId = reference("user_id", UserTable)
}
