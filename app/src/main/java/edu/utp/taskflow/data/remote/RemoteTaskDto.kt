package edu.utp.taskflow.data.remote

data class RemoteTaskDto(
    val id: String,
    val title: String,
    val category: String,
    val priority: String,
    val isCompleted: Boolean,
    val userId: String
)

data class TaskRequestDto (
    val title: String,
    val category: String,
    val priority: String,
    val isCompleted: Boolean,
    val userId: String
)