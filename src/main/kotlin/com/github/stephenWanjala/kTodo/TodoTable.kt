package com.github.stephenWanjala.kTodo

import org.jetbrains.exposed.dao.id.IntIdTable

object TodoTable : IntIdTable() {
    val title = varchar("title", 100)
    val completed = bool("completed")
}
