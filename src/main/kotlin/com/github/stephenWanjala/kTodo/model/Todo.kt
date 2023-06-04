package com.github.stephenWanjala.kTodo.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)

@Serializable
data class UserRequest(
    val username: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Int,
    val username: String
)

@Serializable
data class TodoRequest(
    val title: String,
    val completed: Boolean,
    val isTrainingTask: Boolean
)
@Serializable
data class TodoResponse(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val isTrainingTask: Boolean,
    val userId: Int
)
