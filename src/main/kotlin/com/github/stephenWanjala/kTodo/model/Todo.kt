package com.github.stephenWanjala.kTodo.model

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id:Int? =null,
    val title:String,
    val completed:Boolean,
)
